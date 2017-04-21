<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
				<table width="100%">
					<c:if test="${'31'==applyDetail[0].applyStatus
							or '32'==applyDetail[0].applyStatus 
							or '33'==applyDetail[0].applyStatus
							or '34'==applyDetail[0].applyStatus
							or '41'==applyDetail[0].applyStatus
							or '90'==applyDetail[0].applyStatus}">
                		<tr>
                			<td width="100" class="tdtitle">审批金额：</td>
                			<td width="140"><input type="text" id="apprAmount" name="apprAmount" size="10" value="${ApprOpinion.apprAmount}" class="easyui-numberbox" 
                				data-options="required:true,groupSeparator:',',onChange:function (n,o){validateApprQuota(n,${apprQuota})}" precision="2" max="2000000.00" min="1.00" style="text-align:right;"/>元</td>
                			<td width="100" class="tdtitle">审批利率：</td>
                			<td width="80"><input type="text" id="apprInte" name="apprInte" size="4" value="${ApprOpinion.apprInte}"
                				class="easyui-numberbox" data-options="required:true" precision="2" min="9.85" max="19.40" style="text-align:right;"/>%</td>
                			<td width="100" class="tdtitle">还款方式:</td>
							<td width="100" ><select id="returnKind" name="returnKind" data-options="width:100,panelHeight:45" class="easyui-combobox" editable=false>
								<c:forEach items="${returnKindList}" var="obj">
									<c:if test="${'UM'!=applyDetail[0].channel&&'58'!=applyDetail[0].channel&&'ZZ'!=applyDetail[0].channel}">
										<option value="${obj.itemNo}" <c:if test="${obj.itemNo == ApprOpinion.retuKind}">selected</c:if>>${obj.itemName}</option>
									</c:if>
									<c:if test="${('UM'==applyDetail[0].channel||'58'==applyDetail[0].channel||'ZZ'==applyDetail[0].channel)&&obj.itemNo!='10'}">
										<option value="${obj.itemNo}" <c:if test="${obj.itemNo == ApprOpinion.retuKind}">selected</c:if>>${obj.itemName}</option>
									</c:if>
								</c:forEach>
							</select>	
                		</tr>
                	</c:if>
                		<tr height="50">
                			<td width="90" class="tdtitle"><input type="hidden" id="appNum" name="appNum" value="${ApprOpinion.appNum}"/>审批意见：</td>
                			<td colspan="5" width="530">
                				<input type="radio" id="apprResult01" name="apprResult" onchange="setUseableElements()" value="10" <c:if test="${'10'==ApprOpinion.apprResult}">checked</c:if>>
                					<label id="apprResult01L" style="cursor:pointer" for="apprResult01">通过</label>
                					<input type="hidden" id="passCodeH" name="passCodeH" value="${ApprOpinion.passCode}"/>
									<span id="passCodesSpan">
								    <select id="passCodes" class="easyui-combobox" data-options="width:50,panelHeight:80" editable=false>
								    	<option value="" ></option>
										<c:forEach items="${passCodeList}" var="obj">
											<option value="${obj.itemNo}" >${obj.itemName}</option>
										</c:forEach>
									</select>
									</span>
									
								<input type="radio" id="apprResult02" name="apprResult" onchange="setUseableElements()" value="20" <c:if test="${'20'==ApprOpinion.apprResult}">checked</c:if>>
									<label id="apprResult02L" style="cursor:pointer" for="apprResult02">拒绝</label>
									<input type="hidden" id="refuseCodeH" name="refuseCodeH" value="${ApprOpinion.refuseCode}"/>
									<span id="refuseCodesSpan">
								    <select id="refuseCodes" class="easyui-combobox" data-options="width:50,panelHeight:80" editable=false>
								    	<option value="" ></option>
										<c:forEach items="${refuseCodeList}" var="obj">
											<option value="${obj.itemNo}" >${obj.itemName}</option>
										</c:forEach>
									</select>
									</span>
									
								<!--
								<c:if test="${'31'==applyDetail[0].applyStatus or '33'==applyDetail[0].applyStatus}">
								 复审一/复审二 显示下一阶段 
								<input type="radio" id="apprResult06" name="apprResult" onchange="setUseableElements()" value="50" <c:if test="${'50'==ApprOpinion.apprResult}">checked</c:if>>
									<label id="apprResult06L" style="cursor:pointer" for="apprResult06">下一阶段</label>&nbsp;&nbsp;
								</c:if>
								-->
								<c:if test="${'10'!=applyDetail[0].applyStatus and '35'!=applyDetail[0].applyStatus and '41'!=applyDetail[0].applyStatus}">
								<input type="radio" id="apprResult04" name="apprResult" onchange="setUseableElements()" value="31" <c:if test="${'31'==ApprOpinion.apprResult}">checked</c:if>>
									<label id="apprResult04L" style="cursor:pointer" for="apprResult04">退回补件</label>
								</c:if>
								<c:if test="${'31'==applyDetail[0].applyStatus or '33'==applyDetail[0].applyStatus or '34'==applyDetail[0].applyStatus}">
								<!-- 复审一/复审二/复审三 显示尽调 -->
									<input type="radio" id="apprResult05" name="apprResult" onchange="setUseableElements()" value="40" <c:if test="${'40'==ApprOpinion.apprResult}">checked</c:if>>
										<label id="apprResult05L" style="cursor:pointer" for="apprResult05">展开尽调</label>
								</c:if>
								<c:if test="${'31'==applyDetail[0].applyStatus or '33'==applyDetail[0].applyStatus or '34'==applyDetail[0].applyStatus or '35'==applyDetail[0].applyStatus or '41'==applyDetail[0].applyStatus}">
								<!-- 复审一/复审二/复审三/复审四/尽调 显示回退前手 -->
									<br/>
									<input type="radio" id="apprResult03" name="apprResult" onchange="setUseableElements()" value="30" <c:if test="${'30'==ApprOpinion.apprResult}">checked</c:if>>
										<label id="apprResult03L" style="cursor:pointer" for="apprResult03">回退前手</label>
									<select id="backToInfos" class="easyui-combobox" data-options="width:150,panelHeight:80" editable=false>
										<c:forEach items="${BackRoller}" var="obj">
										<option value="${obj.needInforCodes}" >${obj.custName}</option>
										</c:forEach>
									</select>
								</c:if>
							</td>
                		</tr>
                		<tr>
                			<td class="tdtitle">意见详情<br/>（内部）：</td>
                			<td colspan="5"><textarea id="apprInfo" name="apprInfo" cols="50" rows="5" >${ApprOpinion.apprInfo}</textarea></td>
                		</tr>
                		<tr>
                			<td class="tdtitle">意见详情<br/>（外部）：</td>
                			<td colspan="5">
              					<c:forEach items="${refuseReaonList}" var="obj">
									<input type="checkbox" id="apprInfoExtC${obj.itemNo}" name="apprInfoExtC" value="${obj.itemNo}" <c:if test="${'1' == obj.checked}">checked</c:if> /><label style="cursor:pointer" for="apprInfoExtC${obj.itemNo}">${obj.itemName}</label>
								</c:forEach>
                			</td>
                		</tr>
                		<tr>
                			<td class="tdtitle">备注：</td>
                			<td colspan="5"><textarea id="remark" name="remark" cols="50" rows="2" >${ApprOpinion.remark}</textarea></td>
                		</tr>
                		<tr height="60px">
                			<td colspan="6" align="center">
                			<c:if test="${opflag == '1'}">
                				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="modifyApplyInfo()">修改申请</a>&nbsp;&nbsp;
	                			<c:if test="${'21'!=applyDetail[0].applyStatus and '32'!=applyDetail[0].applyStatus}">
			                	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="openBlackListForReviewWindow()">添加为黑名单</a>&nbsp;&nbsp;
			                	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="saveSignInfo(0)">&nbsp;&nbsp;保存&nbsp;&nbsp;</a>&nbsp;&nbsp;
	                			</c:if>
			                	<c:if test="${'31'==applyDetail[0].applyStatus or '33'==applyDetail[0].applyStatus or '34'==applyDetail[0].applyStatus}">
				                	<a id="btnUpSign" href="javascript:void(0)" class="easyui-linkbutton" onclick="saveSignInfo(2)">&nbsp;&nbsp;上签&nbsp;&nbsp;</a>&nbsp;&nbsp;
			                	</c:if>
			                </c:if>
			                    <a id="btnSummit" href="javascript:void(0)" class="easyui-linkbutton" onclick="saveSignInfo(1)">&nbsp;&nbsp;提交&nbsp;&nbsp;</a>&nbsp;&nbsp;
			                	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="closeSignSubmitWindow()">&nbsp;&nbsp;关闭&nbsp;&nbsp;</a>
                			</td>
                		</tr>
				</table>
<script type="text/javascript">

	//保存和提交
	function saveSignInfo(operFlagSign) {
		//alert('${apprQuota}');
		//return;
		
	    //检查状态
		if (!checkApplyStatusForSign()){
			return;
		}
	    //检查审批信息
	    if (!checkSignInfo()){
	        return;
	    }
	    //保存
	    if (operFlagSign == 0){
	    	signCommit(operFlagSign);
	    	//提交
	    }else{
	    	//审批意见
	    	var apprResult = $("input[name='apprResult']:checked").val();
			//退回补件
			if (apprResult == '31'){
				//打开补件选择画面
				openAddInfoWindow();
				//退回补件以外
			}else{
			    $.messager.confirm("确认", "是否确认提交到下一阶段？", function (r) {
			        if (r) {
			        	signCommit(operFlagSign);
			        }
			    });
			}
	    }
	}
	
	//保存和提交
	function signCommit(operFlagSign) {
	    var reqUrl = "<%=request.getContextPath()%>/sign/saveSignInfo.do";
	    //var row = $('#t').datagrid('getSelected');
	    //var applyStatus = row.applyStatus;
	    var applyStatus = "${applyDetail[0].applyStatus}";
	    var apprResult = $("input[name='apprResult']:checked").val();
	    var vBackToInfos = "";
	    if (apprResult == '30'){	//回退前手人
	    	vBackToInfos = $('#backToInfos').combobox('getValue');
	    }
	    var passCode = "";
	    if (apprResult == '10'){	//通过
	    	passCode = $('#passCodes').combobox('getValue'); 
	    }
	    var refuseCode = "";
	    if (apprResult == '20'){	//拒绝
	    	refuseCode = $('#refuseCodes').combobox('getValue'); 
	    }
	    var apprAmount = "";
	    if($('#apprAmount').length > 0){
	    	apprAmount =getNumberValue('#apprAmount');
	    }
	    var returnKind = "";
	    if($('#returnKind').length > 0){
	    	returnKind = $('#returnKind').combobox('getValue'); 
	    }
	    //审批意见外部
		var aie = $("input[name='apprInfoExtC']:checked").map(function () {
            return $(this).val();
        }).get().join("|");
		//贷款方式
		var loanType = "${applyDetail[0].loanType}";
	    $.messager.progress({
 	        text: '数据处理中，请稍等......',
 	    }); 
		$.post(reqUrl, 
			{operFlagSign: operFlagSign,
			 loanid: "${loanId}",
			 paperId: "${applyDetail[1].paperId}",
			 applyStatus: applyStatus,
			 appNum: $('#appNum').val(),
			 apprAmount: apprAmount,
			 //apprAmount: getNumberValue('#apprAmount'),
			 apprInte: $('#apprInte').val(),
			 apprResult: apprResult,
			 //backToInfos: $('#backToInfos').combobox('getValue'),
			 backToInfos: vBackToInfos,
			 apprInfo: $('#apprInfo').val(),
			 //apprInfoExt: $('#apprInfoExt').val(),
			 apprInfoExt: aie,
			 refuseCode: refuseCode,
			 passCode: passCode,
			 returnKind: returnKind,
			 remark: $('#remark').val(),
			 loanType: loanType,
			}, 
			function(data){
				$.messager.progress('close');
				var msgs= data.split(':');
				var msgCode = msgs[0];
				var msg = msgs[1];
				alert(msg);
				//操作成功
				if (msgCode == '12'){
					//资料审核状态，同意通过时，执行风险初审
					//贷款方式不是资金池模型
					if ((applyStatus == '20' || applyStatus == '21') && apprResult == '10' && loanType != '02'){
						reqUrl = "<%=request.getContextPath()%>/creditApplyRiskCheck/doRiskCheck.do";
						$.post(reqUrl, 
							{loanId: "${loanId}"}
						);
					}
	                //保存以外
				    if (operFlagSign != 0){
						closeSignSubmitWindow();
						queryCreditApply();
				    }
				}
			}
		);
	}
	
	function checkApplyStatusForSign(){
		//var row = $('#t').datagrid('getSelected');
		var applyStatus = "${applyDetail[0].applyStatus}";
		if (applyStatus == '10'){
			alert("受理状态的申请不能签署意见！");
			return false;
		}
		return true;
	}
	
	//检查审批信息
	function checkSignInfo(){
		//审批意见
		var apprResult = $("input[name='apprResult']:checked").val();
		//风险复审和尽调岗
		//var row = $('#t').datagrid('getSelected');
		var applyStatus = "${applyDetail[0].applyStatus}";
		//if (row.applyStatus == '31' || row.applyStatus == '32' || row.applyStatus == '33' || row.applyStatus == '34' || row.applyStatus == '41' ){
		if (applyStatus == '31' || applyStatus == '33' || applyStatus == '34' || applyStatus == '41'){
			//审批通过时
			if (apprResult == '10'){
				//批准金额
				//var apprAmount = $("#apprAmount").val();
				var apprAmount = getNumberValue('#apprAmount');
				//if (!isMoneyInt14Dec2(apprAmount)){
				//	alert("批准金额最大为14位整数，2位小数！");
				//	return false;
				if(typeof(apprAmount)!="undefined" && apprAmount.length==0){
					alert("请填写批准金额！");
					return false;
				}else{
					//北龙中网批复金额不得小于10万
			         if("${applyDetail[0].channel}" == "ZW" && apprAmount < 100000){
                         alert("北龙中网批准金额不得小于10万！");
                         return false;
                   }
					//通过码为907是可以大于
				    var passCode = $('#passCodes').combobox('getValue');
					if(passCode !='907'){
						//var row = $('#t').datagrid('getSelected');
						var applyAmt = ${applyDetail[0].applyAmt};
						if (apprAmount > applyAmt){
							alert("批准金额不能大于申请金额！");
							return false;
						}
					}
				}
				//批准利率
				var apprInte = $("#apprInte").val();
				//if (!isRateInt3Dec6(apprInte)){
				//	alert("批准利率最大为3位整数，6位小数！");
				//	return false;
				if(typeof(apprInte)!="undefined" && apprInte.length==0){
					alert("请填写审批利率！");
					return false;
				}
			}
			
			//回退前手时
			if (apprResult == '30'){
				//回退前手人
				var backToInfos = $('#backToInfos').combobox('getValue');
				//没有选择回退人时
				if (backToInfos == ''){
					alert("请选择回退前手人!");
					return false;
				}
			}
		}
		/*
		//通过
		if (apprResult == '10'){
			//通过代码
			var passCode = $('#passCodes').combobox('getValue');
			//没有通过代码时
			if (passCode == ''){
				alert("请选择通过代码!");
				return false;
			}
		}

		*/
		//提交或上签时，如若是拒绝件，需强制勾选拒绝代码 才可保存上签
		if (apprResult == '20'){
			//拒绝代码
			var refuseCode = $('#refuseCodes').combobox('getValue');
			//没有拒绝代码时
			if (refuseCode == ''){
				alert("请选择拒绝代码!");
				return false;
			}
		}
		//审批意见
		if (typeof(apprResult) == "undefined"){
			alert("请选择审批意见！");
			return false;
		}
		//意见详情（内部）
		var apprInfo = $("#apprInfo").val();
		if(apprInfo == ''){
			alert("请输入意见详情（内部）！");
			return false;
		}else if (getTotalBytes(apprInfo) > 1000){
			alert("意见详情（内部）不能超过1000个字符！");
			return false;
		}
		//意见详情（外部）
		//var apprInfoExt = $("#apprInfoExt").val();
		//通过或回退前手或下一阶段以外时，必输
		//if (apprResult != '10' && apprResult != '30' && apprResult != '50'){
		if (apprResult == '20'){
			/* if(apprInfoExt == ''){
				alert("请输入意见详情（外部）！");
				return false;
			} */
			var aie = $("input[name='apprInfoExtC']:checked").map(function () {
	               return $(this).val();
	           }).get();
			if (aie == ''){
				alert("请选择意见详情（外部）！");
				return false;
			}
		}
		//备注
		var remark = $("#remark").val();
        if (getTotalBytes(remark) > 200){
			alert("备注不能超过200个字符！");
			return false;
		}
		return true;
	}
	
	function validateApprQuota(apprAmount,quota){
	/*
		if (quota < apprAmount){
			var checked = $("input[id='apprResult01']").attr("checked");
			if(checked=="checked"){
				$("input[id='apprResult01']").attr("checked",false);
			}
			$("input[id='apprResult01']").attr("disabled","disabled");
			//$('#apprResult01L').attr("disabled","disabled");
		}else{
			$("input[id='apprResult01']").attr("disabled",false);
		}
	*/
	}
	
	//关闭登录窗口
	function closeSignSubmitWindow() {
	    $('#signSubmitWindow').window('close');
	}
	
    $(function() {
    	//显示或隐藏拒绝码输入框
    	setUseableElements();
    });
    
	//显示或隐藏拒绝码输入框
	function setUseableElements() {
		//审批意见
		var apprResult = $("input[name='apprResult']:checked").val();
		//通过编码和拒绝代码
		if (apprResult == '10'){
			var passCodeHV = $('#passCodeH').val();
			$('#passCodes').val(passCodeHV); 
			$('#passCodesSpan').show();
			$('#refuseCodesSpan').hide();
		}else if (apprResult == '20') {
			$('#passCodesSpan').hide();
			var refuseCodeHV = $('#refuseCodeH').val();
			$('#refuseCodes').val(refuseCodeHV); 
			$('#refuseCodesSpan').show();
		}else{
			$('#passCodesSpan').hide();
			$('#refuseCodesSpan').hide();
		}
		//上签按钮
		//var row = $('#t').datagrid('getSelected');
		var applyStatus = "${applyDetail[0].applyStatus}";
		if (applyStatus == '31' || applyStatus == '33'|| applyStatus == '34'){
			$('#btnUpSign').linkbutton();
			if (apprResult == '10'){
				$('#btnUpSign').linkbutton('enable');
			}else if (apprResult == '20') {
				$('#btnUpSign').linkbutton('enable');
			}else{
				$('#btnUpSign').linkbutton('disable');
			}
		}
		//拒绝
		if (apprResult != '20') {
			$("[name='apprInfoExtC']").removeAttr('checked'); 
		}
	}
	
	
	//开启窗口
	function modifyApplyInfo(){
		$('#modifyApplyWindow').openWin({
			title:'修改授信申请',
			//maximized:true,
			width : 960,
			href:'openCreditMain.do?applyStatus=${applyDetail[0].applyStatus}&loanId=${loanId}&direct2Path=review/editCredit/editCreditMain',
			onLoad : function(){var editMarrSign = $('#editMarrSign').val();switchRequired(editMarrSign,"Edit");}		//根据婚姻状况初始化必输项
			});
		
		return;
	}
	
	//关闭登录窗口
    function closeModifyApply() {
        $('#modifyApplyWindow').window('close');
    }
</script>