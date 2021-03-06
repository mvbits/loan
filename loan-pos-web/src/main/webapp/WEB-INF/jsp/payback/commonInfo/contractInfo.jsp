<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- 协议信息 -->
   		<tr>
   			<td style="width:15%;word-break;" class="tdtitle">客户名称:</td>
   			<td style="width:35%;word-break;"><input type="text" id="detailCustName1" readonly="readonly" value="${contract.custName}" size="30"/></td>
   			<td class="tdtitle">合同编号:</td>
   			<td><input type="text" id="detailContNo" readonly="readonly"  value="${contract.contNo}" size="30"/></td>
   		</tr>
   		<tr>
   		    <td class="tdtitle">证件类型:</td>
   		    <td>
	   		    <select id="paperKindName1" name="paperKindName1" class="detailPaperKind" disabled="disabled">
				<c:forEach items="${paperList}" var="obj">
					<option value="${obj.itemNo}" <c:if test="${obj.itemNo == '10'}">selected</c:if>>${obj.itemName}</option>
				</c:forEach>
				</select>
			</td>
   			<td class="tdtitle">证件号码:</td>
   			<td><input type="text" id="detailPaperId1" readonly="readonly" value="${customer.paperId}" size="30"/></td> 
   		</tr>
   		<tr>
	   		<td class="tdtitle">授信额度:</td>
	   		<td><input type="text" id="detailCreditLine" readonly="readonly" value="${contract.creditLine}"  size="30"
	   			class="easyui-numberbox" data-options="groupSeparator:',',precision:2" /></td> 
	   		<td class="tdtitle">授信利率:</td>
	   		<td><input type="text" id="detailCreditInterest" readonly="readonly" value="${contract.creditInterest}" size="30"/></td> 
   		</tr>
   		<tr>
           	<td class="tdtitle">授信期限:</td>
           	<td><input type="text" id="detailContTerm" readonly="readonly" value="${contract.contTerm}" size="30"/></td> 
		   	<td class="tdtitle">还款方式:</td>
		   	<td>
	   			<select disabled="disabled" id="detailReturnKind" name="detailReturnKind" class="detailReturnKind">
				<c:forEach items="${returnKindList}" var="obj">
					<option value="${obj.itemNo}" <c:if test="${obj.itemNo == contract.paybackMethod}">selected</c:if>>${obj.itemName}</option>
				</c:forEach>
				</select>
			</td> 
   		</tr>
   		<tr>
	   		<td class="tdtitle">协议生效日期:</td>
	   		<td><input type="text"  id="detailAgreementBiginDate" readonly="readonly" value="${contract.beginDateStr}" size="30"/></td> 
	   		<td class="tdtitle">协议到期日期:</td>
	   		<td><input type="text"  id="detailAgreementEndDate" readonly="readonly" value="${contract.endDateStr}" size="30"/></td>
   		</tr>
   		<tr>
   			<td class="tdtitle">收款账号:</td>
   			<td><input type="text" id="detailAccountNo" readonly="readonly" value="${contract.acceptAccount}" size="30"/></td>
   			<td class="tdtitle">账户开户行:</td>
   			<td><input type="text" id="detailBankName1" readonly="readonly" value="${contract.accountOpenBank}" size="30"/></td>
   		</tr>
