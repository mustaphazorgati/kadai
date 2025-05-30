/*
 * Copyright [2025] [envite consulting GmbH]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

package io.kadai.workbasket.internal;

import io.kadai.workbasket.api.WorkbasketAccessItemQuery;
import io.kadai.workbasket.internal.models.WorkbasketAccessItemImpl;
import io.kadai.workbasket.internal.models.WorkbasketSummaryImpl;
import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

/** This class provides a mapper for all queries. */
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:Indentation"})
public interface WorkbasketQueryMapper {

  @Select(
      "<script>"
          + "SELECT "
          + "w.ID, w.KEY, w.NAME, w.DOMAIN, W.TYPE, w.DESCRIPTION, w.OWNER, w.CUSTOM_1, w.CUSTOM_2, w.CUSTOM_3, w.CUSTOM_4, w.ORG_LEVEL_1, w.ORG_LEVEL_2, w.ORG_LEVEL_3, w.ORG_LEVEL_4, w.MARKED_FOR_DELETION, w.CUSTOM_5, w.CUSTOM_6, w.CUSTOM_7, w.CUSTOM_8 from WORKBASKET w "
          + "<if test = 'joinWithAccessList'> "
          + "<choose>"
          + "<when test=\"_databaseId == 'db2'\">"
          + "LEFT OUTER JOIN (select WORKBASKET_ID as WID, MAX(PERM_READ) as MAX_READ, MAX(PERM_READTASKS) as MAX_READTASKS, MAX(PERM_EDITTASKS) as MAX_EDITTASKS, MAX(PERM_OPEN) as MAX_OPEN,  "
          + "MAX(PERM_APPEND) as MAX_APPEND, MAX(PERM_TRANSFER) as MAX_TRANSFER, MAX(PERM_DISTRIBUTE) as MAX_DISTRIBUTE, MAX(PERM_CUSTOM_1) as MAX_CUSTOM_1, MAX(PERM_CUSTOM_2) as MAX_CUSTOM_2, "
          + "MAX(PERM_CUSTOM_3) as MAX_CUSTOM_3, MAX(PERM_CUSTOM_4) as MAX_CUSTOM_4, MAX(PERM_CUSTOM_5) as MAX_CUSTOM_5, MAX(PERM_CUSTOM_6) as MAX_CUSTOM_6, MAX(PERM_CUSTOM_7) as MAX_CUSTOM_7, "
          + "MAX(PERM_CUSTOM_8) as MAX_CUSTOM_8, MAX(PERM_CUSTOM_9) as MAX_CUSTOM_9, MAX(PERM_CUSTOM_10) as MAX_CUSTOM_10, MAX(PERM_CUSTOM_11) as MAX_CUSTOM_11, MAX(PERM_CUSTOM_12) as MAX_CUSTOM_12 "
          + "</when>"
          + "<otherwise>"
          + "LEFT OUTER JOIN (select WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ, MAX(PERM_READTASKS::int) as MAX_READTASKS, MAX(PERM_EDITTASKS::int) as MAX_EDITTASKS, MAX(PERM_OPEN::int) as MAX_OPEN,  "
          + "MAX(PERM_APPEND::int) as MAX_APPEND, MAX(PERM_TRANSFER::int) as MAX_TRANSFER, MAX(PERM_DISTRIBUTE::int) as MAX_DISTRIBUTE, MAX(PERM_CUSTOM_1::int) as MAX_CUSTOM_1, MAX(PERM_CUSTOM_2::int) as MAX_CUSTOM_2, "
          + "MAX(PERM_CUSTOM_3::int) as MAX_CUSTOM_3, MAX(PERM_CUSTOM_4::int) as MAX_CUSTOM_4, MAX(PERM_CUSTOM_5::int) as MAX_CUSTOM_5, MAX(PERM_CUSTOM_6::int) as MAX_CUSTOM_6, MAX(PERM_CUSTOM_7::int) as MAX_CUSTOM_7, "
          + "MAX(PERM_CUSTOM_8::int) as MAX_CUSTOM_8, MAX(PERM_CUSTOM_9::int) as MAX_CUSTOM_9, MAX(PERM_CUSTOM_10::int) as MAX_CUSTOM_10, MAX(PERM_CUSTOM_11::int) as MAX_CUSTOM_11, MAX(PERM_CUSTOM_12::int) as MAX_CUSTOM_12 "
          + "</otherwise>"
          + "</choose>"
          + "FROM WORKBASKET_ACCESS_LIST where ACCESS_ID IN (<if test='accessIds != null'><foreach item='item' collection='accessIds' separator=',' >#{item}</foreach></if>) group by WORKBASKET_ID ) a "
          + "on (w.ID = a.WID)"
          + "</if> "
          + "<where> 1=1 "
          + "<if test='ownerIn != null'>AND w.OWNER IN(<foreach item='item' collection='ownerIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='ownerLike != null'>AND (<foreach item='item' collection='ownerLike' separator=' OR ' >LOWER(w.OWNER) LIKE #{item}</foreach>)</if> "
          + "<if test='idIn != null'>AND w.ID IN(<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyIn != null'>AND w.KEY IN(<foreach item='item' collection='keyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyLike != null'>AND (<foreach item='item' collection='keyLike' separator=' OR ' >LOWER(w.KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='nameIn != null'>AND w.NAME IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR ' >LOWER(w.NAME) LIKE #{item}</foreach>)</if> "
          + "<if test='keyOrNameLike != null'>AND (<foreach item='item' collection='keyOrNameLike' separator=' OR ' >LOWER(w.NAME) LIKE #{item} OR LOWER(w.KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND w.DOMAIN IN(<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >LOWER(w.DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='type!= null'>AND w.TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=' OR ' > ( <if test='item.begin!=null'> w.CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> w.CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='modifiedIn !=null'> AND ( <foreach item='item' collection='modifiedIn' separator=' OR ' > ( <if test='item.begin!=null'> w.MODIFIED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> w.MODIFIED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='descriptionLike != null'>AND (<foreach item='item' collection='descriptionLike' separator=' OR '>LOWER(w.DESCRIPTION) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND w.CUSTOM_1 IN(<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >LOWER(w.CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND w.CUSTOM_2 IN(<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >LOWER(w.CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND w.CUSTOM_3 IN(<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >LOWER(w.CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND w.CUSTOM_4 IN(<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >LOWER(w.CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='custom5In != null'>AND w.CUSTOM_5 IN(<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' >LOWER(w.CUSTOM_5) LIKE #{item}</foreach>)</if> "
          + "<if test='custom6In != null'>AND w.CUSTOM_6 IN(<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' >LOWER(w.CUSTOM_6) LIKE #{item}</foreach>)</if> "
          + "<if test='custom7In != null'>AND w.CUSTOM_7 IN(<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' >LOWER(w.CUSTOM_7) LIKE #{item}</foreach>)</if> "
          + "<if test='custom8In != null'>AND w.CUSTOM_8 IN(<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' >LOWER(w.CUSTOM_8) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel1In != null'>AND w.ORG_LEVEL_1 IN(<foreach item='item' collection='orgLevel1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel1Like != null'>AND (<foreach item='item' collection='orgLevel1Like' separator=' OR ' >LOWER(w.ORG_LEVEL_1) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel2In != null'>AND w.ORG_LEVEL_2 IN(<foreach item='item' collection='orgLevel2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel2Like != null'>AND (<foreach item='item' collection='orgLevel2Like' separator=' OR ' >LOWER(w.ORG_LEVEL_2) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel3In != null'>AND w.ORG_LEVEL_3 IN(<foreach item='item' collection='orgLevel3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel3Like != null'>AND (<foreach item='item' collection='orgLevel3Like' separator=' OR ' >LOWER(w.ORG_LEVEL_3) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel4In != null'>AND w.ORG_LEVEL_4 IN(<foreach item='item' collection='orgLevel4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel4Like != null'>AND (<foreach item='item' collection='orgLevel4Like' separator=' OR ' >LOWER(w.ORG_LEVEL_4) LIKE #{item}</foreach>)</if> "
          + "<if test = 'joinWithAccessList'> "
          + "<if test = 'checkReadPermission'> "
          + "AND (a.MAX_READ = 1 "
          + "</if> "
          + "<if test='permissions != null'>AND "
          + "<if test = '!checkReadPermission'> "
          + "( "
          + "</if> "
          + "<foreach item='permission' collection='permissions' separator=' AND ' >"
          + "<if test=\"permission.name() == 'READ'\">a.MAX_READ</if> "
          + "<if test=\"permission.name() == 'READTASKS'\">a.MAX_READTASKS</if> "
          + "<if test=\"permission.name() == 'EDITTASKS'\">a.MAX_EDITTASKS</if> "
          + "<if test=\"permission.name() == 'OPEN'\">a.MAX_OPEN</if> "
          + "<if test=\"permission.name() == 'APPEND'\">a.MAX_APPEND</if>"
          + "<if test=\"permission.name() == 'TRANSFER'\">a.MAX_TRANSFER</if>"
          + "<if test=\"permission.name() == 'DISTRIBUTE'\">a.MAX_DISTRIBUTE</if>"
          + "<if test=\"permission.name() == 'CUSTOM_1'\">a.MAX_CUSTOM_1</if>"
          + "<if test=\"permission.name() == 'CUSTOM_2'\">a.MAX_CUSTOM_2</if>"
          + "<if test=\"permission.name() == 'CUSTOM_3'\">a.MAX_CUSTOM_3</if>"
          + "<if test=\"permission.name() == 'CUSTOM_4'\">a.MAX_CUSTOM_4</if>"
          + "<if test=\"permission.name() == 'CUSTOM_5'\">a.MAX_CUSTOM_5</if>"
          + "<if test=\"permission.name() == 'CUSTOM_6'\">a.MAX_CUSTOM_6</if>"
          + "<if test=\"permission.name() == 'CUSTOM_7'\">a.MAX_CUSTOM_7</if>"
          + "<if test=\"permission.name() == 'CUSTOM_8'\">a.MAX_CUSTOM_8</if>"
          + "<if test=\"permission.name() == 'CUSTOM_9'\">a.MAX_CUSTOM_9</if>"
          + "<if test=\"permission.name() == 'CUSTOM_10'\">a.MAX_CUSTOM_10</if>"
          + "<if test=\"permission.name() == 'CUSTOM_11'\">a.MAX_CUSTOM_11</if>"
          + "<if test=\"permission.name() == 'CUSTOM_12'\">a.MAX_CUSTOM_12</if> = 1 "
          + "</foreach>"
          + "</if>)"
          + "</if>"
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='orderItem' collection='orderBy' separator=',' >${orderItem}</foreach></if> "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "key", column = "KEY")
  @Result(property = "name", column = "NAME")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "type", column = "TYPE")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  @Result(property = "markedForDeletion", column = "MARKED_FOR_DELETION")
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  List<WorkbasketSummaryImpl> queryWorkbasketSummaries(WorkbasketQueryImpl workbasketQuery);

  @Select(
      "<script>"
          + "SELECT "
          + "WBA.ID, WORKBASKET_ID, WB.KEY, ACCESS_ID, ACCESS_NAME, PERM_READ, PERM_READTASKS, PERM_EDITTASKS, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, "
          + "PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8, PERM_CUSTOM_9, PERM_CUSTOM_10, PERM_CUSTOM_11, PERM_CUSTOM_12 "
          + "from WORKBASKET_ACCESS_LIST WBA "
          + "LEFT JOIN WORKBASKET WB ON WORKBASKET_ID = WB.ID"
          + "<where>"
          + "<if test='idIn != null'>AND WBA.ID IN(<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketIdIn != null'>AND WORKBASKET_ID IN(<foreach item='item' collection='workbasketIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyIn != null'>AND WB.KEY IN(<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyLike != null'>AND (<foreach item='item' collection='workbasketKeyLike' separator=' OR '>LOWER(WB.KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='accessIdIn != null'>AND ACCESS_ID IN(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) </if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='orderItem' collection='orderBy' separator=',' >${orderItem}</foreach></if> "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "workbasketId", column = "WORKBASKET_ID")
  @Result(property = "workbasketKey", column = "KEY")
  @Result(property = "accessId", column = "ACCESS_ID")
  @Result(property = "accessName", column = "ACCESS_NAME")
  @Result(property = "permRead", column = "PERM_READ")
  @Result(property = "permReadTasks", column = "PERM_READTASKS")
  @Result(property = "permEditTasks", column = "PERM_EDITTASKS")
  @Result(property = "permOpen", column = "PERM_OPEN")
  @Result(property = "permAppend", column = "PERM_APPEND")
  @Result(property = "permTransfer", column = "PERM_TRANSFER")
  @Result(property = "permDistribute", column = "PERM_DISTRIBUTE")
  @Result(property = "permCustom1", column = "PERM_CUSTOM_1")
  @Result(property = "permCustom2", column = "PERM_CUSTOM_2")
  @Result(property = "permCustom3", column = "PERM_CUSTOM_3")
  @Result(property = "permCustom4", column = "PERM_CUSTOM_4")
  @Result(property = "permCustom5", column = "PERM_CUSTOM_5")
  @Result(property = "permCustom6", column = "PERM_CUSTOM_6")
  @Result(property = "permCustom7", column = "PERM_CUSTOM_7")
  @Result(property = "permCustom8", column = "PERM_CUSTOM_8")
  @Result(property = "permCustom9", column = "PERM_CUSTOM_9")
  @Result(property = "permCustom10", column = "PERM_CUSTOM_10")
  @Result(property = "permCustom11", column = "PERM_CUSTOM_11")
  @Result(property = "permCustom12", column = "PERM_CUSTOM_12")
  List<WorkbasketAccessItemImpl> queryWorkbasketAccessItems(
      WorkbasketAccessItemQuery accessItemQuery);

  @Select(
      "<script>"
          + "SELECT COUNT(*) from WORKBASKET w "
          + "<if test = 'joinWithAccessList'> "
          + "<choose>"
          + "<when test=\"_databaseId == 'db2'\">"
          + "LEFT OUTER JOIN (select WORKBASKET_ID as WID, MAX(PERM_READ) as MAX_READ, MAX(PERM_READTASKS) as MAX_READTASKS, MAX(PERM_EDITTASKS) as MAX_EDITTASKS, MAX(PERM_OPEN) as MAX_OPEN,  "
          + "MAX(PERM_APPEND) as MAX_APPEND, MAX(PERM_TRANSFER) as MAX_TRANSFER, MAX(PERM_DISTRIBUTE) as MAX_DISTRIBUTE, MAX(PERM_CUSTOM_1) as MAX_CUSTOM_1, MAX(PERM_CUSTOM_2) as MAX_CUSTOM_2, "
          + "MAX(PERM_CUSTOM_3) as MAX_CUSTOM_3, MAX(PERM_CUSTOM_4) as MAX_CUSTOM_4, MAX(PERM_CUSTOM_5) as MAX_CUSTOM_5, MAX(PERM_CUSTOM_6) as MAX_CUSTOM_6, MAX(PERM_CUSTOM_7) as MAX_CUSTOM_7, "
          + "MAX(PERM_CUSTOM_8) as MAX_CUSTOM_8, MAX(PERM_CUSTOM_9) as MAX_CUSTOM_9, MAX(PERM_CUSTOM_10) as MAX_CUSTOM_10, MAX(PERM_CUSTOM_11) as MAX_CUSTOM_11, MAX(PERM_CUSTOM_12) as MAX_CUSTOM_12 "
          + "</when>"
          + "<otherwise>"
          + "LEFT OUTER JOIN (select WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ, MAX(PERM_READTASKS::int) as MAX_READTASKS, MAX(PERM_EDITTASKS::int) as MAX_EDITTASKS, MAX(PERM_OPEN::int) as MAX_OPEN,  "
          + "MAX(PERM_APPEND::int) as MAX_APPEND, MAX(PERM_TRANSFER::int) as MAX_TRANSFER, MAX(PERM_DISTRIBUTE::int) as MAX_DISTRIBUTE, MAX(PERM_CUSTOM_1::int) as MAX_CUSTOM_1, MAX(PERM_CUSTOM_2::int) as MAX_CUSTOM_2, "
          + "MAX(PERM_CUSTOM_3::int) as MAX_CUSTOM_3, MAX(PERM_CUSTOM_4::int) as MAX_CUSTOM_4, MAX(PERM_CUSTOM_5::int) as MAX_CUSTOM_5, MAX(PERM_CUSTOM_6::int) as MAX_CUSTOM_6, MAX(PERM_CUSTOM_7::int) as MAX_CUSTOM_7, "
          + "MAX(PERM_CUSTOM_8::int) as MAX_CUSTOM_8, MAX(PERM_CUSTOM_9::int) as MAX_CUSTOM_9, MAX(PERM_CUSTOM_10::int) as MAX_CUSTOM_10, MAX(PERM_CUSTOM_11::int) as MAX_CUSTOM_11, MAX(PERM_CUSTOM_12::int) as MAX_CUSTOM_12 "
          + "</otherwise>"
          + "</choose>"
          + "FROM WORKBASKET_ACCESS_LIST where ACCESS_ID IN (<if test='accessIds != null'><foreach item='item' collection='accessIds' separator=',' >#{item}</foreach></if>) group by WORKBASKET_ID ) a "
          + "on (w.ID = a.WID)"
          + "</if> "
          + "<where> 1=1 "
          + "<if test='ownerIn != null'>AND w.OWNER IN(<foreach item='item' collection='ownerIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='ownerLike != null'>AND (<foreach item='item' collection='ownerLike' separator=' OR ' >LOWER(w.OWNER) LIKE #{item}</foreach>)</if> "
          + "<if test='idIn != null'>AND w.ID IN(<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyIn != null'>AND LOWER(w.KEY) IN(<foreach item='item' collection='keyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyLike != null'>AND (<foreach item='item' collection='keyLike' separator=' OR ' >LOWER(w.KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='nameIn != null'>AND LOWER(w.NAME) IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR ' >LOWER(w.NAME) LIKE #{item}</foreach>)</if> "
          + "<if test='keyOrNameLike != null'>AND (<foreach item='item' collection='keyOrNameLike' separator=' OR ' >LOWER(w.NAME) LIKE #{item} OR LOWER(w.KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND w.DOMAIN IN(<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >LOWER(w.DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='type!= null'>AND w.TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=' OR ' > ( <if test='item.begin!=null'> w.CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> w.CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='modifiedIn !=null'> AND ( <foreach item='item' collection='modifiedIn' separator=' OR ' > ( <if test='item.begin!=null'> w.MODIFIED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> w.MODIFIED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='descriptionLike != null'>AND (<foreach item='item' collection='descriptionLike' separator=' OR '>LOWER(w.DESCRIPTION) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND LOWER(w.CUSTOM_1) IN(<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >LOWER(w.CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND LOWER(w.CUSTOM_2) IN(<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >LOWER(w.CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND LOWER(w.CUSTOM_3) IN(<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >LOWER(w.CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND LOWER(w.CUSTOM_4) IN(<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >LOWER(w.CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='custom5In != null'>AND LOWER(w.CUSTOM_5) IN(<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' >LOWER(w.CUSTOM_5) LIKE #{item}</foreach>)</if> "
          + "<if test='custom6In != null'>AND LOWER(w.CUSTOM_6) IN(<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' >LOWER(w.CUSTOM_6) LIKE #{item}</foreach>)</if> "
          + "<if test='custom7In != null'>AND LOWER(w.CUSTOM_7) IN(<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' >LOWER(w.CUSTOM_7) LIKE #{item}</foreach>)</if> "
          + "<if test='custom8In != null'>AND LOWER(w.CUSTOM_8) IN(<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' >LOWER(w.CUSTOM_8) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel1In != null'>AND LOWER(w.ORG_LEVEL_1) IN(<foreach item='item' collection='orgLevel1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel1Like != null'>AND (<foreach item='item' collection='orgLevel1Like' separator=' OR ' >LOWER(w.ORG_LEVEL_1) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel2In != null'>AND LOWER(w.ORG_LEVEL_2) IN(<foreach item='item' collection='orgLevel2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel2Like != null'>AND (<foreach item='item' collection='orgLevel2Like' separator=' OR ' >LOWER(w.ORG_LEVEL_2) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel3In != null'>AND LOWER(w.ORG_LEVEL_3) IN(<foreach item='item' collection='orgLevel3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel3Like != null'>AND (<foreach item='item' collection='orgLevel3Like' separator=' OR ' >LOWER(w.ORG_LEVEL_3) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel4In != null'>AND LOWER(w.ORG_LEVEL_4) IN(<foreach item='item' collection='orgLevel4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel4Like != null'>AND (<foreach item='item' collection='orgLevel4Like' separator=' OR ' >LOWER(w.ORG_LEVEL_4) LIKE #{item}</foreach>)</if> "
          + "<if test = 'joinWithAccessList'> "
          + "<if test = 'checkReadPermission'> "
          + "AND (a.MAX_READ = 1 "
          + "</if> "
          + "<if test='permissions != null'>AND "
          + "<if test = '!checkReadPermission'> "
          + "( "
          + "</if> "
          + "<foreach item='permission' collection='permissions' separator=' AND ' >"
          + "<if test=\"permission.name() == 'READ'\">a.MAX_READ</if> "
          + "<if test=\"permission.name() == 'READTASKS'\">a.MAX_READTASKS</if> "
          + "<if test=\"permission.name() == 'EDITTASKS'\">a.MAX_EDITTASKS</if> "
          + "<if test=\"permission.name() == 'OPEN'\">a.MAX_OPEN</if> "
          + "<if test=\"permission.name() == 'APPEND'\">a.MAX_APPEND</if>"
          + "<if test=\"permission.name() == 'TRANSFER'\">a.MAX_TRANSFER</if>"
          + "<if test=\"permission.name() == 'DISTRIBUTE'\">a.MAX_DISTRIBUTE</if>"
          + "<if test=\"permission.name() == 'CUSTOM_1'\">a.MAX_CUSTOM_1</if>"
          + "<if test=\"permission.name() == 'CUSTOM_2'\">a.MAX_CUSTOM_2</if>"
          + "<if test=\"permission.name() == 'CUSTOM_3'\">a.MAX_CUSTOM_3</if>"
          + "<if test=\"permission.name() == 'CUSTOM_4'\">a.MAX_CUSTOM_4</if>"
          + "<if test=\"permission.name() == 'CUSTOM_5'\">a.MAX_CUSTOM_5</if>"
          + "<if test=\"permission.name() == 'CUSTOM_6'\">a.MAX_CUSTOM_6</if>"
          + "<if test=\"permission.name() == 'CUSTOM_7'\">a.MAX_CUSTOM_7</if>"
          + "<if test=\"permission.name() == 'CUSTOM_8'\">a.MAX_CUSTOM_8</if>"
          + "<if test=\"permission.name() == 'CUSTOM_9'\">a.MAX_CUSTOM_9</if>"
          + "<if test=\"permission.name() == 'CUSTOM_10'\">a.MAX_CUSTOM_10</if>"
          + "<if test=\"permission.name() == 'CUSTOM_11'\">a.MAX_CUSTOM_11</if>"
          + "<if test=\"permission.name() == 'CUSTOM_12'\">a.MAX_CUSTOM_12</if> = 1 "
          + "</foreach>"
          + "</if>)"
          + "</if>"
          + "</where>"
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  Long countQueryWorkbaskets(WorkbasketQueryImpl workbasketQuery);

  @Select(
      "<script>SELECT COUNT(*) from WORKBASKET_ACCESS_LIST "
          + "<where>"
          + "<if test='workbasketIdIn != null'>AND WORKBASKET_ID IN(<foreach item='item' collection='workbasketIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='accessIdIn != null'>AND ACCESS_ID IN(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) </if> "
          + "</where>"
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  Long countQueryWorkbasketAccessItems(WorkbasketAccessItemQuery accessItem);

  @Select(
      "<script>SELECT DISTINCT ${columnName} "
          + "FROM WORKBASKET w "
          + "<if test = 'joinWithAccessList'> "
          + "<choose>"
          + "<when test=\"_databaseId == 'db2'\">"
          + "LEFT OUTER JOIN (select WORKBASKET_ID as WID, MAX(PERM_READ) as MAX_READ, MAX(PERM_READTASKS) as MAX_READTASKS, MAX(PERM_EDITTASKS) as MAX_EDITTASKS, MAX(PERM_OPEN) as MAX_OPEN,  "
          + "MAX(PERM_APPEND) as MAX_APPEND, MAX(PERM_TRANSFER) as MAX_TRANSFER, MAX(PERM_DISTRIBUTE) as MAX_DISTRIBUTE, MAX(PERM_CUSTOM_1) as MAX_CUSTOM_1, MAX(PERM_CUSTOM_2) as MAX_CUSTOM_2, "
          + "MAX(PERM_CUSTOM_3) as MAX_CUSTOM_3, MAX(PERM_CUSTOM_4) as MAX_CUSTOM_4, MAX(PERM_CUSTOM_5) as MAX_CUSTOM_5, MAX(PERM_CUSTOM_6) as MAX_CUSTOM_6, MAX(PERM_CUSTOM_7) as MAX_CUSTOM_7, "
          + "MAX(PERM_CUSTOM_8) as MAX_CUSTOM_8, MAX(PERM_CUSTOM_9) as MAX_CUSTOM_9, MAX(PERM_CUSTOM_10) as MAX_CUSTOM_10, MAX(PERM_CUSTOM_11) as MAX_CUSTOM_11, MAX(PERM_CUSTOM_12) as MAX_CUSTOM_12 "
          + "</when>"
          + "<otherwise>"
          + "LEFT OUTER JOIN (select WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ, MAX(PERM_READTASKS::int) as MAX_READTASKS, MAX(PERM_EDITTASKS::int) as MAX_EDITTASKS, MAX(PERM_OPEN::int) as MAX_OPEN,  "
          + "MAX(PERM_APPEND::int) as MAX_APPEND, MAX(PERM_TRANSFER::int) as MAX_TRANSFER, MAX(PERM_DISTRIBUTE::int) as MAX_DISTRIBUTE, MAX(PERM_CUSTOM_1::int) as MAX_CUSTOM_1, MAX(PERM_CUSTOM_2::int) as MAX_CUSTOM_2, "
          + "MAX(PERM_CUSTOM_3::int) as MAX_CUSTOM_3, MAX(PERM_CUSTOM_4::int) as MAX_CUSTOM_4, MAX(PERM_CUSTOM_5::int) as MAX_CUSTOM_5, MAX(PERM_CUSTOM_6::int) as MAX_CUSTOM_6, MAX(PERM_CUSTOM_7::int) as MAX_CUSTOM_7, "
          + "MAX(PERM_CUSTOM_8::int) as MAX_CUSTOM_8, MAX(PERM_CUSTOM_9::int) as MAX_CUSTOM_9, MAX(PERM_CUSTOM_10::int) as MAX_CUSTOM_10, MAX(PERM_CUSTOM_11::int) as MAX_CUSTOM_11, MAX(PERM_CUSTOM_12::int) as MAX_CUSTOM_12 "
          + "</otherwise>"
          + "</choose>"
          + "FROM WORKBASKET_ACCESS_LIST where ACCESS_ID IN (<if test='accessIds != null'><foreach item='item' collection='accessIds' separator=',' >#{item}</foreach></if>) group by WORKBASKET_ID ) a "
          + "on (w.ID = a.WID)"
          + "</if> "
          + "<where>"
          + "1=1 "
          + "<if test='ownerIn != null'>AND w.OWNER IN(<foreach item='item' collection='ownerIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='ownerLike != null'>AND (<foreach item='item' collection='ownerLike' separator=' OR ' >LOWER(w.OWNER) LIKE #{item}</foreach>)</if> "
          + "<if test='idIn != null'>AND w.ID IN(<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyIn != null'>AND LOWER(w.KEY) IN(<foreach item='item' collection='keyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyLike != null'>AND (<foreach item='item' collection='keyLike' separator=' OR ' >LOWER(w.KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='nameIn != null'>AND LOWER(w.NAME) IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR ' >LOWER(w.NAME) LIKE #{item}</foreach>)</if> "
          + "<if test='keyOrNameLike != null'>AND (<foreach item='item' collection='keyOrNameLike' separator=' OR ' >LOWER(w.NAME) LIKE #{item} OR LOWER(w.KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND w.DOMAIN IN(<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >LOWER(w.DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='type!= null'>AND w.TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=' OR ' > ( <if test='item.begin!=null'> w.CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> w.CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='modifiedIn !=null'> AND ( <foreach item='item' collection='modifiedIn' separator=' OR ' > ( <if test='item.begin!=null'> w.MODIFIED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> w.MODIFIED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='descriptionLike != null'>AND (<foreach item='item' collection='descriptionLike' separator=' OR '>LOWER(w.DESCRIPTION) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND w.CUSTOM_1 IN(<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >LOWER(w.CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND w.CUSTOM_2 IN(<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >LOWER(w.CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND w.CUSTOM_3 IN(<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >LOWER(w.CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND w.CUSTOM_4 IN(<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >LOWER(w.CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='custom5In != null'>AND w.CUSTOM_5 IN(<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' >LOWER(w.CUSTOM_5) LIKE #{item}</foreach>)</if> "
          + "<if test='custom6In != null'>AND w.CUSTOM_6 IN(<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' >LOWER(w.CUSTOM_6) LIKE #{item}</foreach>)</if> "
          + "<if test='custom7In != null'>AND w.CUSTOM_7 IN(<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' >LOWER(w.CUSTOM_7) LIKE #{item}</foreach>)</if> "
          + "<if test='custom8In != null'>AND w.CUSTOM_8 IN(<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' >LOWER(w.CUSTOM_8) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel1In != null'>AND w.ORG_LEVEL_1 IN(<foreach item='item' collection='orgLevel1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel1Like != null'>AND (<foreach item='item' collection='orgLevel1Like' separator=' OR ' >LOWER(w.ORG_LEVEL_1) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel2In != null'>AND w.ORG_LEVEL_2 IN(<foreach item='item' collection='orgLevel2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel2Like != null'>AND (<foreach item='item' collection='orgLevel2Like' separator=' OR ' >LOWER(w.ORG_LEVEL_2) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel3In != null'>AND w.ORG_LEVEL_3 IN(<foreach item='item' collection='orgLevel3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel3Like != null'>AND (<foreach item='item' collection='orgLevel3Like' separator=' OR ' >LOWER(w.ORG_LEVEL_3) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel4In != null'>AND w.ORG_LEVEL_4 IN(<foreach item='item' collection='orgLevel4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel4Like != null'>AND (<foreach item='item' collection='orgLevel4Like' separator=' OR ' >LOWER(w.ORG_LEVEL_4) LIKE #{item}</foreach>)</if> "
          + "<if test='markedForDeletion != null'>AND w.MARKED_FOR_DELETION = #{markedForDeletion}</if> "
          + "<if test = 'joinWithAccessList'> "
          + "<if test = 'checkReadPermission'> "
          + "AND (a.MAX_READ = 1 "
          + "</if> "
          + "<if test='permissions != null'>AND "
          + "<if test = '!checkReadPermission'> "
          + "( "
          + "</if> "
          + "<foreach item='permission' collection='permissions' separator=' AND ' >"
          + "<if test=\"permission.name() == 'READ'\">a.MAX_READ</if> "
          + "<if test=\"permission.name() == 'READTASKS'\">a.MAX_READTASKS</if> "
          + "<if test=\"permission.name() == 'EDITTASKS'\">a.MAX_EDITTASKS</if> "
          + "<if test=\"permission.name() == 'OPEN'\">a.MAX_OPEN</if> "
          + "<if test=\"permission.name() == 'APPEND'\">a.MAX_APPEND</if>"
          + "<if test=\"permission.name() == 'TRANSFER'\">a.MAX_TRANSFER</if>"
          + "<if test=\"permission.name() == 'DISTRIBUTE'\">a.MAX_DISTRIBUTE</if>"
          + "<if test=\"permission.name() == 'CUSTOM_1'\">a.MAX_CUSTOM_1</if>"
          + "<if test=\"permission.name() == 'CUSTOM_2'\">a.MAX_CUSTOM_2</if>"
          + "<if test=\"permission.name() == 'CUSTOM_3'\">a.MAX_CUSTOM_3</if>"
          + "<if test=\"permission.name() == 'CUSTOM_4'\">a.MAX_CUSTOM_4</if>"
          + "<if test=\"permission.name() == 'CUSTOM_5'\">a.MAX_CUSTOM_5</if>"
          + "<if test=\"permission.name() == 'CUSTOM_6'\">a.MAX_CUSTOM_6</if>"
          + "<if test=\"permission.name() == 'CUSTOM_7'\">a.MAX_CUSTOM_7</if>"
          + "<if test=\"permission.name() == 'CUSTOM_8'\">a.MAX_CUSTOM_8</if>"
          + "<if test=\"permission.name() == 'CUSTOM_9'\">a.MAX_CUSTOM_9</if>"
          + "<if test=\"permission.name() == 'CUSTOM_10'\">a.MAX_CUSTOM_10</if>"
          + "<if test=\"permission.name() == 'CUSTOM_11'\">a.MAX_CUSTOM_11</if>"
          + "<if test=\"permission.name() == 'CUSTOM_12'\">a.MAX_CUSTOM_12</if> = 1 "
          + "</foreach>"
          + "</if>)"
          + "</if>"
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='orderItem' collection='orderBy' separator=',' >${orderItem}</foreach></if> "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  List<String> queryWorkbasketColumnValues(WorkbasketQueryImpl workbasketQuery);

  @Select(
      "<script>SELECT DISTINCT ${columnName} "
          + "from WORKBASKET_ACCESS_LIST WBA "
          + "LEFT JOIN WORKBASKET WB ON WORKBASKET_ID = WB.ID "
          + "<where>"
          + "<if test='idIn != null'>AND ID IN(<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketIdIn != null'>AND WORKBASKET_ID IN(<foreach item='item' collection='workbasketIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyIn != null'>AND WB.KEY IN(<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyLike != null'>AND (<foreach item='item' collection='workbasketKeyLike' separator=' OR '>LOWER(WB.KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='accessIdIn != null'>AND ACCESS_ID IN(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) </if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='orderItem' collection='orderBy' separator=',' >${orderItem}</foreach></if> "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  List<String> queryWorkbasketAccessItemColumnValues(WorkbasketAccessItemQuery accessItemQuery);
}
