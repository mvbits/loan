<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@	page import="java.util.Date" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
       	<tr>
	       	<td width="15%" class="tdtitle">期望还款日期:</td>
	       	<td width="35%" ><input type="text" class="easyui-datebox" id="expectPaybackDate" name="expectPaybackDate" 
	       			value="<fmt:formatDate value='<%=new Date()%>' pattern='yyyy/MM/dd'/>" size="30"
	       			data-options="onChange:function (n,o){changeDate(n);}"/></td>
	       			
	       <td class="tdtitle">贷款偿还方式:</td>
			<td>
			<select id="loanpaybackway" name="loanpaybackway">
				<c:forEach items="${repayMethodList}" var="obj">
					<option value="${obj.itemNo}" <c:if test="${obj.itemNo == receipt.loanPaybackWay}">selected</c:if>>${obj.itemName}</option>
				</c:forEach>
			</select>
			</td>	
       	</tr>
       	<tr>
        	<td class="tdtitle">归还类型:</td>
        	<td colspan="3">
        		<c:if test="${receipt.paybackWay eq '90'}">
	        		<c:if test="${overFlag eq '0' }">
	        		<input type="radio" name="kind" class="easyui-validatebox" value="0" id="1" onclick="javascript:changeKind('0');">归还指定本金
	        		</c:if>
	        		<input type="radio" name="kind" class="easyui-validatebox" value="1" id="2" onclick="javascript:changeKind('1');">归还指定总额
	        		<input type="radio" name="kind" class="easyui-validatebox" value="2" id="3" onclick="javascript:changeKind('2');">归还剩余本金
        		</c:if>
        		<c:if test="${receipt.paybackWay eq '10' and overFlag eq '1'}">
        			<input type="radio" name="kind" class="easyui-validatebox" value="2" id="4" onclick="javascript:queryRepayPlan('single');">归还最近分期
        		</c:if>
        		<c:if test="${receipt.paybackWay eq '10' and overFlag eq '0'}">
        			<span style="color: red;font-size: 15px">未拖欠的等额本息还款自动处理</span>
        			<!-- <input type="radio" name="kind" class="easyui-validatebox" value="3" id="5" onclick="javascript:queryRepayPlan('batch');">归还剩余本金 -->
        		</c:if>
        	</td>
       	</tr>
       	<tr>
        	<td class="tdtitle">还款金额:</td>
        	<td><input type="text" id="paybackAmount" name="paybackAmount" size="30" style="text-align:right"
        			class="easyui-numberbox" data-options="groupSeparator:',',precision:2,onChange:function (n,o){changeAmnt(n);}"
        			<c:if test="${receipt.paybackWay eq '10' and overFlag eq '0'}"> disabled </c:if> /></td>
        	<td class="tdtitle">还款本金:</td>
        	<td><input type="text" id="paybackPrincipal" name="paybackPrincipal" size="30" style="text-align:right" disabled
        			class="easyui-numberbox" data-options="groupSeparator:',',precision:2"/></td>
       	</tr>
       	<tr>
        	<td class="tdtitle">还款利息:</td>
        	<td><input type="text" id="paybackInterest" name="paybackInterest" size="30" style="text-align:right" disabled
        			class="easyui-numberbox" data-options="groupSeparator:',',precision:2" /></td>
  			<td class="tdtitle">还款罚息:</td>
        	<td><input type="text" id="paybackPenalty" name="paybackPenalty" size="30" style="text-align:right" disabled
        			class="easyui-numberbox" data-options="groupSeparator:',',precision:2" /></td>
		</tr>
       	<tr>
        	<td class="tdtitle">还款总金额:</td>
        	<td><input type="text" id="paybackTotalAmount" name="paybackTotalAmount" size="30" style="text-align:right" disabled
        			class="easyui-numberbox" data-options="groupSeparator:',',precision:2" />
        		<input type="hidden" id="paybackOverFlag" name="paybackOverFlag" value="${overFlag}"/>	
  			</td>
  			<td class="tdtitle">期次:</td>
        	<td>
        		<input type="text" id="term" name="term" style="text-align:right" disabled value="${term}"/>	
  			</td>
       	</tr>
