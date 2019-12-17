import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.rdfconnection.RDFConnectionFactory;



import java.io.FileNotFoundException;
import java.util.List;

//此代码为之前示例demo，不需考虑
public class JenaTest {

    public static void output(Model model) {
        StmtIterator itr = model.listStatements();
        while (itr.hasNext()) {
            System.out.println(itr.nextStatement());
        }
    }

    public static void main(String[] args){
        Model ontologyModel = ModelFactory.createDefaultModel();
        ontologyModel.read(JenaTest.class.getResource("/data/finance-onto.owl").toString());

        // 为实例数据创建Model
        Model dataModel = ModelFactory.createDefaultModel();
        dataModel.read(JenaTest.class.getResource("/data/finance-data.nt").toString());

        // 创建一个新Model将本体与实例数据进行合并
        Model fusionModel = ModelFactory.createDefaultModel();
        fusionModel.add(ontologyModel);
        fusionModel.add(dataModel);

        // 输出推理前的数据
        System.out.println("Triples before reason:");
        output(fusionModel);

        // 在合并后的数据模型上进行OWL推理
        // Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        InfModel inf = ModelFactory.createInfModel(reasoner, fusionModel);

        // 输出推理后的数据
        System.out.println("Triples after reasoning:");
        output(inf.getDeductionsModel());

        ontologyModel.close();
        dataModel.close();
        fusionModel.close();
    }

}
