package com.dugq.pojo.enums;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author dugq
 * @date 2022/7/5 3:25 下午
 */
public enum MapperOpEnums {
   SELECT(1,"select"),
   UPDATE(2,"update"),
   INSERT(3,"insert"),
   DELETE(4,"delete"),
   BATCH_SELECT(5,"select"),
   BATCH_INSERT(6,"insert"),
   ;
   private static final Set<Integer> BATCH_OP = new HashSet<>();
   static {
      BATCH_OP.add(5);
      BATCH_OP.add(6);
      BATCH_OP.add(7);
   }
   public static boolean isBatchOp(Integer type){
      return BATCH_OP.contains(type);
   }


   private final Integer type;
   private final String desc;

   MapperOpEnums(Integer type,String desc) {
      this.type = type;
      this.desc = desc;
   }

   public static MapperOpEnums getByType(Integer type){
      if (Objects.isNull(type)){
         return null;
      }
      for (MapperOpEnums value : values()) {
         if (type==value.getType()){
            return value;
         }
      }
      return null;
   }

   public Integer getType() {
      return type;
   }

   public String getDesc() {
      return desc;
   }
}
