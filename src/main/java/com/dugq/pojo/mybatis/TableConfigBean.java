package com.dugq.pojo.mybatis;

/**
 * @author dugq
 * @date 2022/6/29 12:26 上午
 */
public class TableConfigBean {
   private String tableName;
   private String domain;
   private String subPackage;
   private boolean generateDto;
   private boolean generateParam;

   public String getTableName() {
      return tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public String getDomain() {
      return domain;
   }

   public void setDomain(String domain) {
      this.domain = domain;
   }

   public String getSubPackage() {
      return subPackage;
   }

   public void setSubPackage(String subPackage) {
      this.subPackage = subPackage;
   }

   public boolean isGenerateDto() {
      return generateDto;
   }

   public void setGenerateDto(boolean generateDto) {
      this.generateDto = generateDto;
   }

   public boolean isGenerateParam() {
      return generateParam;
   }

   public void setGenerateParam(boolean generateParam) {
      this.generateParam = generateParam;
   }
}
