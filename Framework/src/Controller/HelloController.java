package Controller;

import Annotations.Controller;
import Annotations.Get;
import Annotations.Url;
import Annotations.RestApi;
import mg.prom16.ModelView;

@Controller
public class HelloController {

    @Get
    @Url(value = "/helloWorld")
    public String helloWorld() {
        return "Hello Workd";
    }
    @Get
    @Url(value = "/helloWorldPage")
    public ModelView helloWorldView() { 
        ModelView modelView = new ModelView();
        modelView.setUrl("/views/helloWorldPage.jsp");
        modelView.addObject("message", "Hello World");
        return modelView;
    }
    @Get
    @RestApi   
    @Url(value = "/helloWorldApi")
    public String helloWorldApi() {
        return "Hello Workd Api";
    }
}
