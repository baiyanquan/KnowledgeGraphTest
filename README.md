# KnowledgeGraphTest

# A Maven Project

## jdk: 1.8（如果使用其他版本的jdk，rdfox的依赖包会出现问题）

# 依赖引入

## jena的依赖通过maven pom文件方式进行引入，rdfox的依赖通过lib方式引入

# 文件说明

## JenaTest: 测试jena的代码，读取本地文件后使用getRDFSReasoner/getOWLReasoner进行推理；

## RDFoxTest: 测试RDFox的代码，使用datalog自定义规则推理;

## FusekiTest：数据库连接（可以正常连接并进行数据库查询，但转换存在问题）

# 代码存在问题

目前代码都可以在正确环境下运行，存在的问题主要在与数据库连接一部分，我还没有找到解决下面问题的方法（这几天会继续找）：

1. 合适的api
	并没有找到合适的api直接读取三元组构造模型，拆分成s、p、o再重组的方式存在问题，即o存在两种，literal和resource，使用getResource或getLiteralImpl会报错（可能是我还没有彻底理解这两个函数以及构造的resource类）
2. 本体模型构建
	之前的demo都存在本体模型文件，即owl文件，内容如下：
	
	<?xml version="1.0" encoding="UTF-8"?>
		<!DOCTYPE rdf:RDF [
		<!ENTITY finance "http://www.example.org/kse/finance#">
		<!ENTITY owl "http://www.w3.org/2002/07/owl#">
		<!ENTITY rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#">
		<!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#">
		<!ENTITY xsd "http://www.w3.org/2001/XMLSchema#">
	]>
	<rdf:RDF xml:base="&finance;"
         xmlns:owl="&owl;"
         xmlns:rdf="&rdf;"
         xmlns:rdfs="&rdfs;">

	<!-- Ontology Information -->
	<owl:Ontology rdf:about=""/>

	<owl:Class rdf:ID="PublicCompany">
		<rdfs:subClassOf rdf:resource="Company"/>
	</owl:Class>


	<owl:ObjectProperty rdf:ID="control">
		<rdfs:domain rdf:resource="Person"/>
		<rdfs:range rdf:resource="Company"/>
	</owl:ObjectProperty>

	</rdf:RDF>
	
	在数据库中尚未确定其位置（还是说不需要这个文件，直接按照三元组中的信息来操作？）