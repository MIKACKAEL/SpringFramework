package mg.prom16;


import java.util.HashSet;
import java.util.Set;

public class Mapping {
    private Set<Verb> verbs ;

    public Mapping() {
        this.verbs = new HashSet<Verb>();
    }
    public Set<Verb> getVerbs() {
        return verbs;
    }
    public void addVerb(Verb verb) throws Exception {
        for (Verb v : verbs) {
            if (v.gethttpVerb().equalsIgnoreCase(verb.gethttpVerb())) {
                throw new Exception("Conflit de verbe.");
            }
        }
        verbs.add(verb);
    }
    public Verb getByHttpverb(String httpVerb) {
        for (Verb v : verbs) {
            if (v.gethttpVerb().equalsIgnoreCase(httpVerb)) {
                return v;
            }
        }
        return null;
    }


}