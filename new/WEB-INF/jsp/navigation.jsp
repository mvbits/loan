<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head id="Head1">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>哈尔滨银行互联网金融业务管理系统</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
    <link href="<%=request.getContextPath()%>/css/default.css" rel="stylesheet" type="text/css" />

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/themes/icon.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src='<%=request.getContextPath()%>/js/XiuCai.index.js'> </script>
	<script type="text/javascript" src='<%=request.getContextPath()%>/js/locale/easyui-lang-zh_CN.js'></script>
	<script type="text/javascript" src='<%=request.getContextPath()%>/js/common_uiext.js'></script>

    <script type="text/javascript">
    <% //String privileges = (String)session.getAttribute("assignedPrivileges"); 
    com.hrbb.loan.web.security.entity.AccessPrivilege access = (com.hrbb.loan.web.security.entity.AccessPrivilege)session.getAttribute("accessPrivilege");
    %>
	var _menus = {
	"menus": [{
		"menuid": "9",
		"menuname": "我的工作台",
		"icon": "icon-min-edit",
		"menus": [{
			"menuid": "91",
			"menuname": "我的任务",
			"icon": "icon-edit",
			"url": "<%=request.getContextPath()%>/workbench/myTasks.do?"
		}]
		},
		<%if (access.hasAnyPrivilege("ROLE_APPLY;ROLE_INFO;ROLE_APPLY_QUERY")) {%>
		{
		"menuid": "1",
		"icon": "icon-tip",
		"menuname": "授信申请管理",
		"menus": [
			<%if(access.hasAnyPrivilege("ROLE_APPLY;ROLE_INFO;")){		//申请管理权限%>
		    {
				"menuid": "11",
				"menuname": "待处理申请",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyNav.do?applyStatus=00"
			},
			{
				"menuid": "16",
				"menuname": "受理中申请",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyNav.do?applyStatus=10"
			},
			{
				"menuid": "12",
				"menuname": "审批中申请",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyNav.do?isApplyStatus=999"
			},
			{
				"menuid": "13",
				"menuname": "审批通过申请",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyNav.do?applyStatus=90"
			},
			{
				"menuid": "14",
				"menuname": "被否决申请",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyNav.do?applyStatus=92"
			},
			{
				"menuid": "15",
				"menuname": "已取消申请",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyNav.do?applyStatus=93"
			},
			<%} %>
			<%if(access.hasAnyPrivilege("ROLE_APPLY_QUERY")){		//申请管理权限%>
				{
					"menuid": "19",
					"menuname": "所有申请",
					"icon": "icon-min-edit",
					"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyNav.do?"
				}
			<%}%>
			]
	},
	<%} %>
	<% if (access.hasAnyPrivilege("ROLE_INFO_APPR;ROLE_APPR_LV1;ROLE_APPR_LV2;ROLE_APPR_LV3;ROLE_ISSUE_APPR;ROLE_DUE_DILI_ADMIN")) {%>
	{
		"menuid": "3",
		"menuname": "审查审批",
		"icon": "icon-edit",
		"url": "demo.html",
		"menus": [
		 <%
		 if(access.hasAnyPrivilege("ROLE_INFO_APPR;ROLE_APPR_LV1;ROLE_APPR_LV2;ROLE_APPR_LV3")){		//授信审批权限
		 %> 
		 {
			"menuid": "31",
			"menuname": "授信审批",
			"icon": "icon-min-edit",
			"child": [{
				"menuid": "311",
				"menuname": "当前工作",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyForReview.do?opflag=1"
			},{
				"menuid": "312",
				"menuname": "已完成工作",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCreditApplyForReview.do?opflag=9"
			}]
		},
		<%
		}
		if( access.hasAnyPrivilege("ROLE_ISSUE_APPR")){			//用款审核权限
		%>
		{
			"menuid": "32",
			"menuname": "用款审核",
			"icon": "icon-min-edit",
			"child": [{
				"menuid": "321",
				"menuname": "当前审核工作",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryPaymentReview.do?reviewStatus=0"
			 },
			 {
				"menuid": "322",
				"menuname": "已完成审核工作",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryPaymentReview.do?reviewStatus=1"	 
			 }]
		}
		<%
		}
		%>
		]
	},
	<%} %>
	<% if (access.hasAnyPrivilege("ROLE_CONTRACT;ROLE_APPROVED;ROLE_APPROVED_QUERY;ROLE_CONTRACT_QUERY")) {%>
	{
		"menuid": "4",
		"icon": "icon-tip",
		"menuname": "协议签约管理",
		"menus": [
		<%if(access.hasAnyPrivilege("ROLE_APPROVED;ROLE_APPROVED_QUERY;")){		//批复管理
		%> 
		{
			"menuid": "41",
			"menuname": "授信审批结果",
			"icon": "icon-users",
			"child": [
			<%if(access.hasAnyPrivilege("ROLE_APPROVED;")){		//批复管理
			%> 
			{
				"menuid": "411",
				"menuname": "待确认批复",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryContractManagementNav1.do?approveStatus=01"
			},{
				"menuid": "412",
				"menuname": "已确认批复",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryContractManagementNav2.do?approveStatus=02"
			},
			{
				"menuid": "413",
				"menuname": "已拒绝批复",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryContractManagementNav3.do?approveStatus=03"
			},
			{
				"menuid": "414",
				"menuname": "已失效批复",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryContractManagementNav4.do?approveStatus=09"
			},
			<%
			}
			if(access.hasAnyPrivilege("ROLE_APPROVED_QUERY;")){		//运营查询权限
			%>
			{
				"menuid": "419",
				"menuname": "所有批复",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryContractManagementNav1.do?approveStatus=99"
			}
			<%} %>
			]
		},
		<%
		}
		if(access.hasAnyPrivilege("ROLE_CONTRACT;ROLE_CONTRACT_QUERY")){		//协议管理
		%>
		{
			"menuid": "42",
			"menuname": "协议管理",
			"icon": "icon-edit",
			"child": [
			<%
			if(access.hasAnyPrivilege("ROLE_CONTRACT")){		//协议管理
			%>
			{
				"menuid": "421",
				"menuname": "待处理协议",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryAgreeMentNavi.do?agreementStatus=06"
			},{
				"menuid": "422",
				"menuname": "已生效协议",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryAgreeMentNavi.do?agreementStatus=01"
			},
			{
				"menuid": "423",
				"menuname": "已冻结协议",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryAgreeMentNavi.do?agreementStatus=02"
			},
			{
				"menuid": "424",
				"menuname": "已失效协议",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryAgreeMentNavi.do?agreementStatus=09"
			},
			{
				"menuid": "425",
				"menuname": "已中止协议",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryAgreeMentNavi.do?agreementStatus=03"
			},
			<%
			}
			if(access.hasAnyPrivilege("ROLE_CONTRACT_QUERY")){
			%>
			{
				"menuid": "429",
				"menuname": "所有协议",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryAgreeMentNavi.do?agreementStatus=99"
			}
			<%}%>
			]
		}
		<%}%>
		]
	},
	<%} %>
	<% if (access.hasAnyPrivilege("ROLE_ISSUE;ROLE_ISSUE_QUERY;")) {%>
	{
		"menuid": "5",
		"icon": "icon-tip",
		"menuname": "用款管理",
		"menus": [
				<%
				if(access.hasAnyPrivilege("ROLE_ISSUE")){
				%>
		         {
					 "menuid" : "52",
					 "menuname": "待处理用款",
					 "icon": "icon-min-edit",
					 "url": "<%=request.getContextPath()%>/navigation/queryPaymentApplyNav.do?paymentStatus=00"
				 },
				 {
					 "menuid": "53",
					 "menuname": "审核中用款",
					 "icon": "icon-min-edit",
					 "url": "<%=request.getContextPath()%>/navigation/queryPaymentApplyNav.do?paymentStatus=10"
				 },
				 {
					 "menuid": "54",
					 "menuname": "审核通过用款",
					 "icon": "icon-min-edit",
					 "child":[{
								 "menuid":"541",
								 "menuname":"待放款",
								 "icon": "icon-min-edit",
								 "url": "<%=request.getContextPath()%>/navigation/queryPaymentApplyNav.do?paymentStatus=90&excuteStatus=0"
					 	      },
					 	      {
					 	    	 "menuid":"542",
								 "menuname":"已放款",
								 "icon": "icon-min-edit",
								 "url": "<%=request.getContextPath()%>/navigation/queryPaymentApplyNav.do?paymentStatus=90&excuteStatus=1"
					 	      }]
				 },
				 {
					 "menuid": "55",
					 "menuname": "被否决用款",
					 "icon": "icon-min-edit",
					 "url": "<%=request.getContextPath()%>/navigation/queryPaymentApplyNav.do?paymentStatus=92"
				 },
				 {
					 "menuid": "56",
					 "menuname": "已取消用款",
					 "icon": "icon-min-edit",
					 "url": "<%=request.getContextPath()%>/navigation/queryPaymentApplyNav.do?paymentStatus=93"
				 },
				<%
				}
				if(access.hasAnyPrivilege("ROLE_ISSUE_QUERY")){
				%>
				{
					 "menuid": "59",
					 "menuname": "所有用款",
					 "icon": "icon-min-edit",
					 "url": "<%=request.getContextPath()%>/navigation/queryPaymentApplyNav.do?paymentStatus=99"
				 }
				<%
				}
				%>
				 ]
	},
	<%} 
	
	if (access.hasAnyPrivilege("ROLE_POSTED;ROLE_POSTED_QUERY;ROLE_REPAY;ROLE_REPAY_QUERY")) {		//授信后管理权限
	%>
	{
		"menuid": "8",
		"icon": "icon-tip",
		"menuname": "授信后管理",
		"menus": [
		<%if (access.hasAnyPrivilege("ROLE_POSTED;ROLE_POSTED_QUERY;")) {		//授信后管理权限
		%>
		{
			"menuid": "81",
			"menuname": "授信台账管理",
			"icon": "icon-tip",
			"child": [
			<%if (access.hasAnyPrivilege("ROLE_POSTED;")) {%>
			{
				"menuid": "811",
				"menuname": "未结清业务",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/unClearedBusiness.do?clearStatus=02"
			},{
				"menuid": "812",
				"menuname": "已结清业务",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/clearedBusiness.do?clearStatus=01"
			},
			<%}
			if (access.hasAnyPrivilege("ROLE_POSTED_QUERY;")) {
			%>
			{
				"menuid": "819",
				"menuname": "所有业务",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/unClearedBusiness.do?clearStatus=99"
			}
			<%}%>
			]
		},
		<%
		}
		if (access.hasAnyPrivilege("ROLE_REPAY;ROLE_REPAY_QUERY")) {
		%>
		{
			"menuid": "82",
			"menuname": "还款申请管理",
			"icon": "icon-role",
			"child": [
			<%if (access.hasAnyPrivilege("ROLE_REPAY;")) {%>
			{
				"menuid": "821",
				"menuname": "未发送指令申请",
				"icon": "icon-set",
				"url": "<%=request.getContextPath()%>/navigation/unSentApply.do?paybackStatus=00"
			},{
				"menuid": "822",
				"menuname": "待扣款还款 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/waitToDeductPayback.do?paybackStatus=10"
			},{
				"menuid": "823",
				"menuname": "扣款成功还款 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/deductSucPayback.do?paybackStatus=20"
			},{
				"menuid": "824",
				"menuname": "扣款失败还款 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/deductFailPayback.do?paybackStatus=90"
			},{
				"menuid": "825",
				"menuname": "已取消还款 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/canceledPayback.do?paybackStatus=30"
			},
			<%
			}
			if (access.hasAnyPrivilege("ROLE_REPAY_QUERY;")) {
			%>
			{
				"menuid": "829",
				"menuname": "所有还款 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/unSentApply.do?paybackStatus=99"
			}
			<%}%>
			]
		},
		<%
		}
		if (access.hasAnyPrivilege("ROLE_REPAY;ROLE_REPAY_QUERY")) {
		%>
		{
			"menuid": "83",
			"menuname": "主动还款进项管理",
			"icon": "icon-role",
			"child": [
			<%if (access.hasAnyPrivilege("ROLE_REPAY;")) {%>
		    {
				"menuid": "831",
				"menuname": "未匹配还款进项 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/notMatchedPaybackImport.do?viewStatus=01"
			},{
				"menuid": "832",
				"menuname": "已匹配还款进项",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/matchedPaybackImport.do?viewStatus=02"
			},
			<%
			}
			if (access.hasAnyPrivilege("ROLE_REPAY_QUERY;")) {
			%>
			 {
					"menuid": "839",
					"menuname": "所以还款进项 ",
					"icon": "icon-role",
					"url": "<%=request.getContextPath()%>/navigation/notMatchedPaybackImport.do?viewStatus=99"
				}
			<%}%>
			]
		},
		<%
		}
		%>
		{
			"menuid": "83",
			"menuname": "还款流水管理",
			"icon": "icon-role",
			"child": [
		    {
				"menuid": "831",
				"menuname": "待确认存疑流水 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/notConfirmedQuestionRunning.do?runningStatus=20"
			},{
				"menuid": "832",
				"menuname": "已匹配还款流水",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/matchedPaybackRunning.do?runningStatus=10"
			},
			{
				"menuid": "832",
				"menuname": "确认待冲销流水",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/confirmToOffsetRunning.do?runningStatus=20"
			},
			{
				"menuid": "832",
				"menuname": "确认已冲销流水",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/confirmedOffsetRunning.do?runningStatus=20"
			}]
		}
		]
	},
	
	<%}
	
	if (access.hasAnyPrivilege("ROLE_CUST;ROLE_CUSTSERVICE;ROLE_ADMIN;")) {			//客户管理/客服管理
	%>
	{
		"menuid": "6",
		"icon": "icon-tip",
		"menuname": "综合业务管理",
		"menus": [
		<% if (access.hasAnyPrivilege("ROLE_CUSTSERVICE;ROLE_ADMIN;")) {%>         
		{
			"menuid": "61",
			"menuname": "客服外呼任务",
			"icon": "icon-tip",
			"child": [{
				"menuid": "612",
				"menuname": "待处理任务",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCallingTaskForReview.do?opflag=1"
			},{
				"menuid": "613",
				"menuname": "已处理任务",
				"icon": "icon-min-edit",
				"url": "<%=request.getContextPath()%>/navigation/queryCallingTaskForReview.do?opflag=0"
			}]
		},
		<% 
		}
		if (access.hasAnyPrivilege("ROLE_CUST;ROLE_ADMIN;")) {%>
		{
			"menuid": "62",
			"menuname": "客户管理",
			"icon": "icon-role",
			"child": [
			{
				"menuid": "621",
				"menuname": "客户管理",
				"icon": "icon-set",
				"url": "<%=request.getContextPath()%>/navigation/queryCustomerNav.do?"
			},{
				"menuid": "622",
				"menuname": "客户亲属 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/queryCustRelaNav.do?"
			},{
				"menuid": "623",
				"menuname": "客户商户 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/queryCustMerchantNav.do?"
			},{
				"menuid": "624",
				"menuname": "客户银行卡 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/queryCustBankNav.do?"
			}]
		},
		{
			"menuid": "63",
			"menuname": "黑名单管理",
			"icon": "icon-role",
			"url": "<%=request.getContextPath()%>/navigation/queryBlacklistNav.do?"
		},	
		<%} %>
		
		<% if (access.hasAnyPrivilege("ROLE_ADMIN;")) {%>
		{
			"menuid": "64",
			"menuname": "业务数据管理",
			"icon": "icon-role",
			"child": [
			{
				"menuid": "641",
				"menuname": "模型结果导入",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/modelResultImport.do?"
			},{
				"menuid": "642",
				"menuname": "业务数据导出 ",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/navigation/bizDataPageNav.do?"
			}]
		},
		<%} %>
		]
	},
	<% 
	}
	
	if(access.hasAnyPrivilege("ROLE_ADMIN;ROLE_SYS_ADMIN;")) {%>
	{
		"menuid": "7",
		"icon": "icon-tip",
		"menuname": "系统管理",
		"menus": [
		<% if(access.hasAnyPrivilege("ROLE_ADMIN;")) {%>       
		   {
			"menuid": "71",
			"menuname": "用户管理",
			"icon": "icon-users",
			"child": [
			
			{
				"menuid": "711",
				"menuname": "用户管理",
				"icon": "icon-users",
				"url": "<%=request.getContextPath()%>/admin/user.do"

			},
			{
				"menuid": "712",
				"menuname": "用户组管理",
				"icon": "icon-role",
				"url": "<%=request.getContextPath()%>/admin/role.do"
			},
			
			]//end of 'menuid 21' 
		},
		<%} %>
		<% if(access.hasAnyPrivilege("ROLE_SYS_ADMIN;")) {%>
		{
			"menuid": "72",
			"menuname": "权限管理",
			"icon": "icon-users",
			"child": [
			{
				"menuid": "721",
				"menuname": "权限管理",
				"icon": "icon-privilege",
				"url": "<%=request.getContextPath()%>/admin/privilege.do"
			}
			]
		}
		<%} %>
		]
	}//end of 'menuid 2'
	<% } %>
	]
};

        //设置修改密码窗口
        /*
        function openPwd() {
            $('#w').window({
                title: '修改密码',
                width: 300,
                modal: true,
                shadow: true,
                closed: true,
                height: 160,
                resizable:false
            });
        }
        */
        
        //关闭修改密码窗口
        function closePwd() {
            $('#w').window('close');
        }

        //修改密码
        function serverLogin() {
        	var $oldpass = $('#txtOldPass');
            var $newpass = $('#txtNewPass');
            var $rePass = $('#txtRePass');

            if ($oldpass.val() == '') {
                msgShow('系统提示', '请输入原密码！', 'warning');
                return false;
            }
            if ($newpass.val() == '') {
                msgShow('系统提示', '请输入新密码！', 'warning');
                return false;
            }
            if ($rePass.val() == '') {
                msgShow('系统提示', '请在一次输入密码！', 'warning');
                return false;
            }

            if ($newpass.val() != $rePass.val()) {
                msgShow('系统提示', '两次密码不一至！请重新输入', 'warning');
                return false;
            }

            var reqUrl = "<%=request.getContextPath()%>/user/modPassword.do";
            $.post(reqUrl,
            		{oldpass:$oldpass.val(),
            		newpass:$newpass.val()}, 
            	function(data) {
            	var obj = eval('(' +data+')');
            	var flg = obj.status;
            	if(flg){
            		msgShow('系统提示', "密码修改成功.", 'info');
            		closePwd();
            	}else{
            		msgShow('系统提示', obj.msg, 'warning');
            	}
            })
            
        }
        
        function openPwdWin(){
        	$('#w').openWin({
				title:'修改密码',
				width:350,
				height:200,
				href:'<%=request.getContextPath()%>/navigation/directAccess.do?srcpath=workbench/PwdModifyView'
				});
        }

        $(function() {
/*
            openPwd();

            $('#editpass').click(function() {
                $('#w').window('open');
            });

            $('#btnEp').click(function() {
                serverLogin();
            })

			$('#btnCancel').click(function(){closePwd();})
*/
			$('#editpass').click(function() {
				openPwdWin();
			});
            
            $('#loginOut').click(function() {
                $.messager.confirm('系统提示', '您确定要退出本次登录吗?', function(r) {

                    if (r) {
                        location.href = '<%=request.getContextPath()%>/j_spring_security_logout';
                    }
                });
            })
            
            //默认展示我的工作台
            addTab(_menus.menus[0].menus[0].menuname,_menus.menus[0].menus[0].url,_menus.menus[0].icon);
        });
		
		

    </script>

</head>
<body class="easyui-layout" style="overflow-y: hidden"  fit="true"   scroll="no">
<noscript>
<div style=" position:absolute; z-index:100000; height:2046px;top:0px;left:0px; width:100%; background:white; text-align:center;">
    <img src="images/noscript.gif" alt='抱歉，请开启脚本支持！' />
</div></noscript>

<div id="loading-mask" style="position:absolute;top:0px; left:0px; width:100%; height:100%; background:#D2E0F2; z-index:20000">
<div id="pageloading" style="position:absolute; top:50%; left:50%; margin:-120px 0px 0px -120px; text-align:center;  border:2px solid #8DB2E3; width:200px; height:40px;  font-size:14px;padding:10px; font-weight:bold; background:#fff; color:#15428B;"> 
    <img src="images/loading.gif" align="absmiddle" /> 正在加载中,请稍候...</div>
</div>

    <div region="north" split="true" border="false" style="overflow: hidden; height: 30px;
        background: url(images/layout-browser-hd-bg.gif) #7f99be repeat-x center 50%;
        line-height: 20px;color: #fff; font-family: Verdana, 微软雅黑,黑体">
        <span style="float:right; padding-right:20px;" class="head">欢迎 
        <%
            Object obj = session.getAttribute("USER");
            if(obj!=null && obj instanceof com.hrbb.loan.web.security.entity.User){
            com.hrbb.loan.web.security.entity.User user = (com.hrbb.loan.web.security.entity.User)obj;
        %>
        <%= user.getLoginName() %>
        <%  } %>
        <a href="#" id="editpass">修改密码</a> <a href="#" id="loginOut">安全退出</a></span>
        <span style="padding-left:10px; font-size: 16px; "><img src="images/blocks.gif" width="20" height="20" align="absmiddle" /> 哈尔滨银行互联网金融业务管理系统</span>
    </div>
    <div region="south" split="true" style="height: 30px; background: #D2E0F2; ">
        
    </div>
    <div region="west" split="true"  title="导航菜单" style="width:180px;" id="west">
			<div id="nav">
		<!--  导航内容 -->
				
			</div>

    </div>
    <div id="mainPanle" region="center" style="background: #eee; overflow-y:hidden">
        <div id="tabs" class="easyui-tabs"  fit="true" border="false" >
			
		</div>
    </div>
    
    
    <!--修改密码窗口
    <div id="w" class="easyui-window" title="修改密码" collapsible="false" minimizable="false"
        maximizable="false" icon="icon-save"  style="width: 300px; height: 150px; padding: 5px;
        background: #fafafa;">
        <div class="easyui-layout" fit="true">
            <div region="center" border="false" style="padding: 10px; background: #fff; border: 1px solid #ccc;">
                <table cellpadding=3>
                    <tr>
                        <td>新密码：</td>
                        <td><input id="txtNewPass" type="Password" class="txt01" /></td>
                    </tr>
                    <tr>
                        <td>确认密码：</td>
                        <td><input id="txtRePass" type="Password" class="txt01" /></td>
                    </tr>
                </table>
            </div>
            <div region="south" border="false" style="text-align: right; height: 30px; line-height: 30px;">
                <a id="btnEp" class="easyui-linkbutton" icon="icon-ok" href="javascript:void(0)" >
                    确定</a> <a id="btnCancel" class="easyui-linkbutton" icon="icon-cancel" href="javascript:void(0)">取消</a>
            </div>
        </div>
    </div>
	-->
	<div id="w"></div>
	
	<div id="mm" class="easyui-menu" style="width:150px;">
		<div id="refresh">刷新</div>
		<div class="menu-sep"></div>
		<div id="close">关闭</div>
		<div id="closeall">全部关闭</div>
		<div id="closeother">除此之外全部关闭</div>
		<div class="menu-sep"></div>
		<div id="closeright">当前页右侧全部关闭</div>
		<div id="closeleft">当前页左侧全部关闭</div>
		<div class="menu-sep"></div>
		<div id="exit">退出</div>
	</div>


</body>
</html>