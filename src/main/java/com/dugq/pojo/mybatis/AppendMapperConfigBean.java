package com.dugq.pojo.mybatis;

import com.dugq.pojo.enums.MapperOpEnums;

import java.util.List;

public class AppendMapperConfigBean {

   /**
    * @see MapperOpEnums
    */
   private Integer opEnums;

   private String methodName;

   private List<String> selectColumns;

   private List<String> updateColumns;

   private List<String> insertColumns;

   private List<String> whereColumns;

   private String generateParamName;

   private String generateEntityName;

   private String generateDtoName;

   private String desc;


   public String getMethodName() {
      return methodName;
   }

   public void setMethodName(String methodName) {
      this.methodName = methodName;
   }

   public Integer getOpEnums() {
      return opEnums;
   }

   public void setOpEnums(Integer opEnums) {
      this.opEnums = opEnums;
   }

   public List<String> getSelectColumns() {
      return selectColumns;
   }

   public void setSelectColumns(List<String> selectColumns) {
      this.selectColumns = selectColumns;
   }

   public List<String> getUpdateColumns() {
      return updateColumns;
   }

   public void setUpdateColumns(List<String> updateColumns) {
      this.updateColumns = updateColumns;
   }

   public List<String> getInsertColumns() {
      return insertColumns;
   }

   public void setInsertColumns(List<String> insertColumns) {
      this.insertColumns = insertColumns;
   }

   public List<String> getWhereColumns() {
      return whereColumns;
   }

   public void setWhereColumns(List<String> whereColumns) {
      this.whereColumns = whereColumns;
   }

   public String getGenerateParamName() {
      return generateParamName;
   }

   public void setGenerateParamName(String generateParamName) {
      this.generateParamName = generateParamName;
   }

   public String getGenerateEntityName() {
      return generateEntityName;
   }

   public void setGenerateEntityName(String generateEntityName) {
      this.generateEntityName = generateEntityName;
   }

   public String getGenerateDtoName() {
      return generateDtoName;
   }

   public void setGenerateDtoName(String generateDtoName) {
      this.generateDtoName = generateDtoName;
   }

   public String getDesc() {
      return desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }
}
