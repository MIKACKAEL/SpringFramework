package mg.prom16;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import com.google.gson.Gson;

import Annotations.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class FrontController extends HttpServlet {
    protected List<Class<?>> list_controller = new ArrayList<>();
    protected Map<String, Mapping> urlMappings = new HashMap<>();

    protected void getControllerList(String package_name) throws ServletException, ClassNotFoundException {
        String bin_path = "WEB-INF/classes/" + package_name.replace(".", "/");
        //
        bin_path = getServletContext().getRealPath(bin_path);
        File b = new File(bin_path);
        list_controller.clear();
        
        for (File onefile : b.listFiles()) {
            if (onefile.isFile() && onefile.getName().endsWith(".class")) {
                Class<?> clazz = Class.forName(package_name + "." + onefile.getName().split(".class")[0]);
                if (clazz.isAnnotationPresent(Controller.class))

                list_controller.add(clazz);

                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(Url.class)) {
                        String url = method.getAnnotation(Url.class).value();
                        if (!method.isAnnotationPresent(Get.class) && !method.isAnnotationPresent(Post.class)) {
                            addMapping(url, clazz, method, "GET");
                        }
                        
                        if (method.isAnnotationPresent(Get.class)) {
                            addMapping(url, clazz, method, "GET");
                        }
                        else if (method.isAnnotationPresent(Post.class)) {
                            addMapping(url, clazz, method, "POST");
                        }
                        // Mapping mapping = new Mapping(clazz.getName(), method);
                        // String key = method.getAnnotation(Get.class).value();  
                        // if (urlMappings.containsKey(key)) {
                        //     throw new ServletException("La methode '"+urlMappings.get(key).getMethod().getName()+"' possede deja l'URL '"+key+"' comme annotation, donc elle ne peux pas etre assigner a la methode '"+mapping.getMethod().getName()+"'");                        }                   
                        // else{
                        //     urlMappings.put(key, mapping);
                        // }
                    }
                }
            }
        }
    }

    private void addMapping(String url, Class<?> clazz, Method method, String verb) throws ServletException {
        if (urlMappings.containsKey(url)) {
            Mapping mapping = urlMappings.get(url);
            try {
                mapping.addVerb(new Verb(clazz.getName(), method, verb));
            } catch (Exception e) {
                throw new ServletException(
                    e + " La methode '" + mapping.getByHttpverb(verb).getMethod().getName() +
                    "' avec URL '" + url + "' est deja utilisee pour la methode " +
                    mapping.getByHttpverb(verb).gethttpVerb() + ". Conflit avec la methode '" +
                    method.getName() + "' avec URL '" + url + "' pour le verbe '" + verb + "'.");
            }
        } else {
            Mapping map = new Mapping();
            try {
                map.addVerb(new Verb(clazz.getName(), method, verb));
            } catch (Exception e) {
                throw new ServletException(e);
            }
            urlMappings.put(url, map);
        }
    }

    protected Object invoke_Method(HttpServletRequest request, String className, Method method) throws IOException, NoSuchMethodException {
        Object returnValue = null;
        try {
            Class<?> clazz = Class.forName(className);
            method.setAccessible(true);

            Parameter[] methodParams = method.getParameters();
            Object[] args = new Object[methodParams.length];

            Enumeration<String> params = request.getParameterNames();
            Map<String, String> paramMap = new HashMap<>();

            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                paramMap.put(paramName, request.getParameter(paramName));
            }
            for (int i = 0; i < methodParams.length; i++) {
                if (methodParams[i].getType().equals(MySession.class)) {
                    HttpSession session = request.getSession();
                    MySession mySession = new MySession(session);
                    args[i] = mySession;
                } else if (methodParams[i].isAnnotationPresent(ObjectAnnotation.class)) {
                    Class<?> paramType = methodParams[i].getType();
                    Object paramObject = paramType.getDeclaredConstructor().newInstance();
                    for (Field field : paramType.getDeclaredFields()) {
                        String paramName = field.isAnnotationPresent(AttributAnnotation.class) ? field.getAnnotation(AttributAnnotation.class).value() : field.getName();
                        if (paramMap.containsKey(paramName)) {
                            field.setAccessible(true);
                            field.set(paramObject, paramMap.get(paramName));
                        }
                    }
                    args[i] = paramObject;
                } else if (methodParams[i].isAnnotationPresent(Param.class)) {
                    String paramName = methodParams[i].getAnnotation(Param.class).name();
                    String paramValue = paramMap.get(paramName);
                    args[i] = paramValue;
                }
                else{
                    if (paramMap.containsKey(methodParams[i].getName())) {
                        args[i] = paramMap.get(methodParams[i].getName());
                    } else {
                        args[i] = null;
                    }
                }
            }

            Object instance = clazz.getDeclaredConstructor().newInstance();
            returnValue = method.invoke(instance, args);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            String package_name = "controllerPackage"; 
            String pack = getServletContext().getInitParameter(package_name);
            if (pack == null) {
                throw new ServletException("Le package \""+package_name+"\" n'est pas reconnu.");
            } else {
                getControllerList(pack);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // response.setContentType("text/html;charset=UTF-8");

        String url = request.getRequestURI().substring(request.getContextPath().length());

        Mapping mapping = urlMappings.get(url);

            if (mapping != null) {
                if (mapping.getByHttpverb(request.getMethod()) !=null) {
                    String httpverb = request.getMethod();
                    Verb verb = mapping.getByHttpverb(httpverb);
                                  
                    try {
                        Object returnValue = invoke_Method(request, verb.getClassName(), verb.getMethod());
                        Gson gson =new Gson();
                        if (verb.getMethod().isAnnotationPresent(RestApi.class)) {
                            response.setContentType("appliction/gson");
                            try(PrintWriter out=response.getWriter()){
                                if (returnValue instanceof ModelView) {
                                    ModelView modelView = (ModelView) returnValue;
                                    HashMap<String, Object> data = modelView.getData();
                                    String jsonData = gson.toJson(data);

                                    out.print(jsonData);
                                } else {
                                    String jsonData = gson.toJson(returnValue);
                                    out.println(jsonData);
                                }
                            }
                        } else {
                            response.setContentType("text/html;charset=UTF-8");
                            if (returnValue instanceof String) {
                                try (PrintWriter out = response.getWriter()) {
                                    out.println("<p>Contenue de la methode <strong>"+verb.getMethod().getName()+"</strong> : "+(String) returnValue+"</p>");
                                }
                            } else if (returnValue instanceof ModelView) {
                                ModelView modelView = (ModelView) returnValue;
                                String viewUrl = modelView.getUrl();
                                HashMap<String, Object> data = modelView.getData();
            
            
                                for (Map.Entry<String, Object> entry : data.entrySet()) {
                                    request.setAttribute(entry.getKey(), entry.getValue());
                                }
            
                                RequestDispatcher dispatcher = request.getRequestDispatcher(viewUrl);
                                dispatcher.forward(request, response);
            
                            } 
                            else if (returnValue == null) {
                                throw new ServletException("La methode \""+verb.getMethod().getName()+"\" retourne une valeur NULL");
                            }
                            else {
                                throw new ServletException("Le type de retour de l'objet \""+returnValue.getClass().getName()+"\" n'est pas pris en charge par le Framework");
                            }     
                            
                        }
        
                            
                    } catch (NoSuchMethodException | IOException e) {
                        throw new ServletException("Erreur lors de l'invocation de la methode \""+verb.getMethod().getName()+"\"", null);
                    }
                }
                else {
                    
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"Auccune methode HTTP " + request.getMethod() + " pour l'URL " + url);
                }
  
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"Pas de methode Get associer a l'URL: \"" + url +"\"");
            }
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
} 