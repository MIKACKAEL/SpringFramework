package Controller;

import Annotations.Controller;
import Annotations.Get;

@Controller
public class HelloController {

    @Get(value = "/helloWorld")
    public void helloWorld() {
        
    }
}
