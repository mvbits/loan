<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/themes/icon.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/locale/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common_uiext.js"></script>
	<script type="text/javascript" src='<%=request.getContextPath()%>/js/calendar.js'></script>
	<script type="text/javascript" >	
	function queryOccurSuspend(){
		var queryUrl="<%=request.getContextPath()%>/acctManager/queryOccurSuspend.do";

			$('#tt').datagrid({
				url : queryUrl,
				queryParams : {
					acDate : $('#acDate').datebox("getValue"),
				}
			});

		}
	
	$(function() {		
		$('#tt').datagrid({
			onClickCell: function (rowIndex, field, value) { 
	            if(field == 'hh'){
	            	$(this).datagrid('unselectAll');
	            }
	            
	        },
		});
	});
	//日期格式化
	function dateFormat(value, row, index){
		return timeStamp2String(value);
	}
	
	function rowformater(value, row, index) {
		var sendFlag = row.sendFlag;
		if(sendFlag == 'F'){
	        return "<a href='javascript:keepAcccounts();'>发起记账</a>";
		}
    }
	
	//开启窗口
	function keepAcccounts(){
		var row = $('#tt').datagrid('getSelected');
		var listId = row.listId;
		var loanAcNo = row.loanAcNo;
		if(confirm("确定要发起记账吗？")){
			document.location.href="<%=request.getContextPath()%>/acctManager/handleKeepAcccounts.do?listId="+listId+"&loanAcNo="+loanAcNo;
		}	
	}
	
	$(function() {
		var msg = $("#resultMsg").html();
		if(msg !=null && msg.length >0){
			alert(msg);
		}
	});
</script>
</head>
<body>
	<div id="tb" style="padding:3px;background:#f4f4f4">
	<fieldset>
		<legend class='dialog-label'>查询条件</legend>
		<span>记账日期:</span> 
		<input id="acDate" name="acDate" class="easyui-datetimebox" style="line-height: 20px; border: 1px solid #ccc" />
		<div>
			<a href="javascript:void(0)" class="easyui-linkbutton"
				onclick="queryOccurSuspend()" iconCls="icon-search" plain="true">查询</a>
		</div>
	</fieldset>
	</div>	
	<table id="tt" class="easyui-datagrid" style="width:5000px;height:600px"
			url="<%=request.getContextPath()%>/acctManager/queryOccurSuspend.do"
			title="核算对账记录列表" iconCls="icon-search" toolbar="#tb" onClickRow="clickRow"
			rownumbers="true" pagination="true">
		<thead>
			<tr>
				<th field="loanAcNo" width="150px">分户账号</th>
				<th field="listId" width="250px">借据编号</th>
				<th field="acDate" formatter="dateFormat" width="100px">记账日期</th>
				<th field="sendFlag" width="90px">发送标志</th>
				<th field="sendNum" width="60px">发送次数</th>
				<th field="sendTime" formatter="dateFormat" width="90px">发送时间</th>
				<th field="recordType" width="60px">类型</th>
				<th field="hh" width="90"  align="center" formatter="rowformater">操作</th>
			</tr>
		</thead>
	</table>
	<div id="resultMsg" style="display:none;">${result}</div>	
</body>
</html>