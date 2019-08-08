import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.system.Txn;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodeQuery {
    public static void main(String[] args) throws FileNotFoundException {
        RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://10.60.38.173:3030//DevKGData/query");

        //查询的服务名称（只需要更改字符串内容即可查询其他service节点）
        String serviceName = "<http://services/10.60.38.181/sock-shop/carts>";

        //SPARQL查询
        Query query = QueryFactory.create("SELECT ?provideS ?nodeNameP ?nodeNameO \n" +
                "WHERE {\n" +
                "  ?provideS ?provideP " + serviceName + " .\n" +
                "  FILTER ( contains(str(?provideP), \"provides\")) .\n" +
                "  ?provideS ?nodeNameP ?nodeNameO .\n" +
                "  FILTER ( contains(str(?nodeNameP), \"nodeName\"))\n" +
                "}");

        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
            QueryExecution qExec = conn.query(query);
            ResultSet rs = qExec.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String subject = qs.get("provideS").toString();
                String object = qs.get("nodeNameO").toString();
                String predicate = qs.get("nodeNameP").toString();
                System.out.println("SPARQL查询结果（object为最终寻找的节点）：");
                System.out.println("<" + subject + ">" + " " + "<" + predicate + ">" + " " + object);
            }
        }


        //先推理后查询
        Model model = ModelFactory.createDefaultModel();

        String userDefinedRules = "[rule1: (?X nodeName ?Y) (?X provides ?Z) -> (?Z belongs ?Y)] ";

        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
            Query reasonQuery = QueryFactory.create("SELECT DISTINCT ?s ?p ?o { ?s ?p ?o }");

            QueryExecution qExec = conn.query(reasonQuery);

            ResultSet rs = qExec.execSelect();

            while (rs.hasNext()) {
                QuerySolution qs = rs.next() ;
                String subject = qs.get("s").toString();
                String object = qs.get("o").toString();
                String predicate = qs.get("p").toString();

                if(predicate.contains("nodeName")) {
                    model.add(model.createResource(subject), model.createProperty("nodeName"), model.createResource(object));
                }
                else if(predicate.contains("provides")) {
                    model.add(model.createResource(subject), model.createProperty("provides"), model.createResource(object));
                }
            }
            qExec.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(userDefinedRules));
        reasoner.setDerivationLogging(true);
        InfModel inf = ModelFactory.createInfModel(reasoner, model);

        Query finalQuery = QueryFactory.create("SELECT DISTINCT ?o { "+ serviceName + " ?p ?o }");

        QueryExecution qExec = QueryExecutionFactory.create(finalQuery, inf);

        ResultSet rs = qExec.execSelect();

        System.out.println("推理结果：");
        while (rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            String object = qs.get("o").toString();
            System.out.println(object);
        }
    }
}
