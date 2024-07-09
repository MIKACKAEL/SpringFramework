package Controller;

import Annotations.Controller;
import Annotations.Get;
import Annotations.ObjectAnnotation;
import Annotations.Param;
import mg.prom16.ModelView;
import mg.prom16.MySession;
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

    @Get("/employe_session")
    public String get_employe_session(@Param(name = "id") String id, @ObjectAnnotation MySession mySession) {
        String sessionId = mySession.getSession().getId();
        return "Employee ID: " + id + ", Session ID: " + sessionId;
    }

    @Get("/employe_session_data")
    public ModelView get_employe_data(@Param(name = "id") String id, @ObjectAnnotation MySession mySession) {
        ModelView modelView = new ModelView();
        modelView.setUrl("/employeeView.jsp");

        modelView.addObject("employeeId", id);
        modelView.addObject("sessionId", mySession.getSession().getId());

        return modelView;
    }
}
