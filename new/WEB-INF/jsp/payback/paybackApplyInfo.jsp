<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
     <div title="还款申请" style="padding:20px;">
          还款申请信息
                	<table>
                	
                	<tr>
                	<td>*贷款总余额:</td>
                	<td><input type="text" class="insertSignLoanTotalBalance" readonly= "true " name="insertSignLoanTotalBalance"/></td>
                	<td>*到期日:</td>
                	<td><input type="text" class="insertSignEndDate" readonly= "true " name="insertSignEndDate"/></td>
                	</tr>
                	<tr>
                	<td>*还款方式:</td>
                	<td><select disabled="disabled" class="insertSignPaybackWay"
							name="insertSignPaybackWay">
								<c:forEach items="${returnKindList}" var="obj">
									<option value="${obj.itemNo}">${obj.itemName}</option>
								</c:forEach>
							</select></td>
                	<td>*贷款偿还方式:</td>
                	<td><select disabled="disabled" class="insertSignLoanPaybackWay"
							name="insertSignLoanPaybackWay">
								<c:forEach items="${repayMethodList}" var="obj">
									<option value="${obj.itemNo}">${obj.itemName}</option>
								</c:forEach>
							</select></td>
                	</tr>
                	<tr>
                	<td>*期望还款日期:</td>
                	<td><input type="text" class="insertSignExpectPaybackDate" readonly= "true " name="insertSignExpectPaybackDate"/></td>
                	<td>*还款总金额:</td>
                	<td><input type="text" class="insertSignPaybackTotalAmount" readonly= "true " name="insertSignPaybackTotalAmount"/></td>
                	</tr>
                	<tr>
                	<td>*还款本金:</td>
                	<td><input type="text" class="insertSignPaybackAmount" readonly= "true " name="insertSignPaybackAmount"/></td>
                	<td>*还款利息:</td>
                	<td><input type="text" class="insertSignPaybackInterest" readonly= "true " name="insertSignPaybackInterest"/></td>
                	</tr>
                	</table>
    </div>