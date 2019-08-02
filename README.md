# KnowledgeGraphTest

# A Maven Project

## jdk: 1.8（如果使用其他版本的jdk，rdfox的依赖包会出现问题）

# 依赖引入

## jena的依赖通过maven pom文件方式进行引入，rdfox的依赖通过lib方式引入

# 文件说明

## JenaTest: 测试jena的代码，读取本地文件后使用getRDFSReasoner/getOWLReasoner进行推理；

## RDFoxTest: 测试RDFox的代码，使用datalog自定义规则推理;

## FusekiTest：数据库连接（可以正常连接并进行数据库查询，但转换存在问题），实现了container到service的推理

# 代码存在问题

目前代码都可以在正确环境下运行，并且解决了以下问题：

1. 连接fuseki并读取三元组
2. 提取信息并进行推理
3. 将数据写入fuseki数据库（目前写在本地数据库中）

本体文件：
根据查找的资料及官方示例理解，如果想要根据rdfs、owl内置规则进行推理（subclass、type等规则），需要构建本体文件；
如果采取自定义规则推理机，不需要此文件。

