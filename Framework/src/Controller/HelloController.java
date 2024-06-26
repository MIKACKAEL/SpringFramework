package Controller;

import Annotations.Controller;
import Annotations.Get;
import mg.prom16.ModelView;

@Controller
public class HelloController {

    @Get(value = "/helloWorld")
    public String helloWorld() {
        return "Hello Workd";
    }

    @Get(value = "/helloWorldPage")
    public ModelView helloWorldView() { 
        ModelView modelView = new ModelView();
        modelView.setUrl("/views/helloWorldPage.jsp");
        modelView.addObject("message", "Hello World");
        return modelView;
    }
}
