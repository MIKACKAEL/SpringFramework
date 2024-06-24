package Controller;

import Annotations.Controller;
import Annotations.Get;
import Annotations.Param;
import mg.prom16.ModelView;
import model.Employe;
@Controller
public class EmployeController {

    @Get(value = "/emloye")
    public ModelView getEmploye(@Param(name = "nom")String nom, @Param(name = "prenom")String prenom, @Param(name = "poste")String poste){
        ModelView mv = new ModelView();
        mv.setUrl("/views/employePage.jsp");
        mv.addObject("nom", nom);
        mv.addObject("prenom", prenom);
        mv.addObject("poste", poste);
        return mv;
    }

    @Get(value = "/emloyeObjet")
    public ModelView getEmploye(Employe employe){
        ModelView mv = new ModelView();
        mv.setUrl("/views/employePage.jsp");
        mv.addObject("employe", employe);
        return mv;
    }
}
