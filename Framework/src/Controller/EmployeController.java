package Controller;

import Annotations.Controller;
import Annotations.Get;
import mg.prom16.ModelView;

@Controller
public class EmployeController {

    @Get(value = "/emloye")
    public ModelView getEmploye(String nom, String prenom, String poste){
        ModelView mv = new ModelView();
        mv.setUrl("/views/employePage.jsp");
        mv.addObject("nom", nom);
        mv.addObject("prenom", prenom);
        mv.addObject("poste", poste);
        return mv;
    }
}
