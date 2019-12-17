import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
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


//此代码为属性路径表达式demo，不需考虑
public class PropertyPathQuery {
    public static void main(String[] args) throws FileNotFoundException {
        RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://10.60.38.173:3030//DevKGData/query");

        //查询的服务名称（只需要更改字符串内容即可查询其他service节点）
        String serviceName = "<http://services/10.60.38.181/sock-shop/carts>";

        //SPARQL查询
        Query query = QueryFactory.create("SELECT * \n" +
                "WHERE {\n" +
                "  ?provideS ?provideP " + serviceName + " .\n" +
                "  FILTER ( contains(str(?provideP), \"provides\")) .\n" +
                "}");

        try (RDFConnectionFuseki conn = (RDFConnectionFuseki) builder.build()) {
            QueryExecution qExec = conn.query(query);
            ResultSet rs = qExec.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String subject = qs.get("provideS").toString();
                String path1 = subject + "/provides";
                String path2 = subject + "/nodeName";
                System.out.println(subject);
                //属性路径查询
                Query propertyPathQuery = QueryFactory.create("PREFIX : " + "<" + subject + "/> \n" +
                        "SELECT * " +
                        "{" +
                        serviceName + " ^:provides/:nodeName ?o ." +
                        "}");
                QueryExecution qExecPropertyPath = conn.query(propertyPathQuery);
                ResultSet rsPropertyPath = qExecPropertyPath.execSelect();
                while (rsPropertyPath.hasNext()) {
                    QuerySolution qs1 = rsPropertyPath.next();
                    String node1 = qs1.get("o").toString();
                    System.out.println(node1);
                }
//                String object = qs.get("nodeNameO").toString();
//                String predicate = qs.get("nodeNameP").toString();
//                System.out.println("SPARQL查询结果（object为最终寻找的节点）：");
//                System.out.println("<" + subject + ">" + " " + "<" + predicate + ">" + " " + object);
            }
        }
    }
}
