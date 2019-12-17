# KnowledgeGraphTest

# A Maven Project

## jdk: 1.8（如果使用其他版本的jdk，rdfox的依赖包会出现问题）

# 依赖引入

## jena的依赖通过maven pom文件方式进行引入，rdfox的依赖通过lib方式引入

# 文件说明

## JenaTest: 测试jena的代码，读取本地文件后使用getRDFSReasoner/getOWLReasoner进行推理；

## RDFoxTest: 测试RDFox的代码，使用datalog自定义规则推理;

## resource.EntityExtraction：数据库连接（可以正常连接并进行数据库查询，但转换存在问题），实现了container到service的推理

## NodeQuery：实现从service到节点的查询（包括了直接使用sparql查询和先推理再使用sparql查询）

# 代码功能

目前代码都可以在正确环境下运行，并且解决了以下问题：

1. 连接fuseki并读取三元组
2. 提取信息并进行推理
3. 将数据写入fuseki数据库（目前写在本地数据库中）

本体文件：
根据查找的资料及官方示例理解，如果想要根据rdfs、owl内置规则进行推理（subclass、type等规则），需要构建本体文件；
如果采取自定义规则推理机，不需要此文件。

根据service查询node节点：
1. 查找论文及相关资料，得出查找rdf图路径普遍采取的有三种方式：自动机+分布式；sparql正则化路径查询；推理+查询
   但自动机+分布式并没有在jena中找到支持此项的代码，没有使用；
2. sparql正则化路径查询：
   首先尝试使用正则表达式，以pod中的nodeName这一property作为关键字，找到含有nodeName的字符串（service到pod只用了provide这一属性，且由于已知命名空间，不需要正则化），出现问题，经多次尝试，发现正则化（regex）只支持搜索literal类型的变量，即属性字符串（例如可搜索"abc"中的"ab"，不能搜索<html://www.baidu.com>中的"bai"，即使将其转换成字符串也无效）
   此问题并未在官方文档中找到任何说明（文档关于这里的说明只有查询literal的例子），在非正式文档中找到一些正则查找suject（jena中体现为resource）,实际尝试失败，更换方式；
   后考虑重组字符串，用字符串拼接的方式，但考虑效率仍决定优化；
   目前使用的方式，是函数方式，sparql中的contain函数类似于java中字符串的contain，将p转化成字符串后查找是否含有nodeName，解决问题；
3. 推理+查询
   推理+查询思路和实现方式都比较简单，先将所有的三元组取出推理（由于pod与service有provides的关系，没有使用container推理，使用了node、pod、service这三者之间的关系进行推理），然后查询推理后的关系；
   疑惑就是，这样做的效率是否过低？还是说只推理一次写回数据库，之后直接查询新有的predicate，以存储空间换查询时间？