<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!--流水信息录入-->
<%-- <div id="callingTaskDetailWindow" class="easyui-window" title="外呼任务" closed="true" collapsible="false" minimizable="false" maximizable="true" icon="icon-save"
style="width: 1000px; height: 500px; padding: 5px;background: #fafafa;">
	 <div class="easyui-layout" fit="true">
    	<div id="callingTaskDetailWindow" region="center" class="easyui-tabs" border="false" style="padding: 10px; background: #fff; border: 1px solid #ccc;">
    	
    			<jsp:include page="../outBound/callingTaskDetail.jsp"></jsp:include>
				<jsp:include page="../creditApply/detailCredit/detailCreditInfo.jsp"></jsp:include>
		</div>
    	
		<div region="south" border="false" style="text-align: center; height: 30px; line-height: 30px;">
    	
			<a id="btnEpinsert" onclick="completeCallingTaskClimer()" class="easyui-linkbutton" icon="icon-ok" href="javascript:void(0)">处理完成</a>
	
			<a id="btnCancel" onclick="closeCallingTaskDetailWindow()" class="easyui-linkbutton" icon="icon-cancel"	href="javascript:void(0)">放弃处理</a>
		</div>
	 </div>
 </div> --%>

     <div id="callingTaskDetail"  title="接受审批结果" collapsible="false" minimizable="false" closed="true"
        maximizable="true" icon="icon-save"  class="easyui-tabs" border="false" style="padding: 10px;overflow :auto; background: #fff; border: 1px solid #ccc;"> 
               <jsp:include page="../outBound/callingTaskDetail.jsp"></jsp:include>
        <c:choose>
		<c:when test="${'APP'==taskObj.relaBizPhase}">
            <jsp:include page="../creditApply/detailCredit/detailCreditInfo.jsp"></jsp:include>
		</c:when>
		<c:when test="${'APR'==taskObj.relaBizPhase}">
		    <div title="审批结果" style="padding:20px;">
				<jsp:include page="../contract/sign/approveInfo.jsp"></jsp:include>
		    </div>
		</c:when>
		<c:when test="${'ISS'==taskObj.relaBizPhase}">
			<jsp:include page="../paymentApply/detailPaymentApply/detailPaymentApplyInfo.jsp"></jsp:include>
		</c:when>
   		<c:otherwise>
   		</c:otherwise>
   		</c:choose>
	</div>
	