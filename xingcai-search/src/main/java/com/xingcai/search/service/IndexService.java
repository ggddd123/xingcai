package com.xingcai.search.service;


public interface IndexService {


    public Boolean addCourseIndex(String indexName,String id,Object object);



    public Boolean updateCourseIndex(String indexName,String id,Object object);


    public Boolean deleteCourseIndex(String indexName,String id);

}
