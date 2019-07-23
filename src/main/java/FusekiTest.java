import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.rdfconnection.RDFConnectionFactory;



import java.io.FileNotFoundException;
import java.util.List;

public class FusekiTest {
    public static void main(String[] args) throws FileNotFoundException {
        RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://10.60.38.173:3030//DevKGData/query");

        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {

            conn.queryResultSet("SELECT DISTINCT ?s ?p ?o { ?s ?p ?o } limit 100", ResultSetFormatter::out);
//            List<Statement> abc = ResultSetFormatter::toList(conn.query("SELECT DISTINCT ?s ?p ?o { ?s ?p ?o } limit 25").execSelect());
//            if(abc.hasNext()){
//                QuerySolution a = abc.next();
//            }
//            conn.querySelect("SELECT DISTINCT ?s ?p ?o { ?s ?p ?o } limit 25", (qs)->{
//                Resource subject = qs.getResource("s") ;
//                System.out.println("Subject: "+subject) ;
//                Resource predicate = qs.getResource("p") ;
//                System.out.println("Predicate: "+predicate) ;
//                Resource object = qs.getResource("o") ;
//                System.out.println("Object: "+object) ;
//            }); ;
        }
    }
}
