<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx" type="com.dianping.cat.report.page.app.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.cat.report.page.app.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.cat.report.page.app.Model" scope="request" />
<a:mobile>
	<link rel="stylesheet" type="text/css" href="${model.webapp}/js/jquery.datetimepicker.css"/>
	<link rel="stylesheet" type="text/css" href="${model.webapp}/assets/css/select2.css"/>
	<link rel="stylesheet" type="text/css" href="${model.webapp}/assets/css/chosen.css"/>
	
	<script src="${model.webapp}/js/jquery.datetimepicker.js"></script>
	<script src="${model.webapp}/assets/js/select2.min.js"></script>
	<script src="${model.webapp}/assets/js/chosen.jquery.min.js"></script>
	
	<div class="report">
		<c:set var="navUrlPrefix" value="op=${payload.action.name}&query1=${payload.query1}"/> 
		<table class="table ">
		<tr>
		<td colspan="2">
				<div class="input-group" style="float:left;">
	              <span class="input-group-addon">开始</span>
	              <input type="text" id="time" style="width:130px"/>
	            </div>
				<div class="input-group" style="float:left;width:60px">
	              <span class="input-group-addon">结束</span>
        	      <input type="text" id="time2" style="width:60px;"/></div>
				 <div class="input-group" style="float:left;width:60px">
	              <span class="input-group-addon">平台</span>  
				<select id="platform" style="width: 80px;height:33px">
					<option value='-1'>ALL</option>
					<option value='1'>Android</option>
					<option value='2'>IOS</option>
				</select></div>
				  <div class="input-group" style="float:left;width:60px">
	              <span class="input-group-addon">APP Name</span>  
				<select id="appName" style="width: 200px; height:33px">
						<c:forEach var="appName" items="${model.crashLogDisplayInfo.appNames}">
							<option value="${appName.title}">${appName.title}</option>
						</c:forEach>
				</select></div>
				    <div class="input-group" style="float:left;">
					<span class="input-group-addon">Dpid</span>
					<input type="text"  id="dpid" />
	            </div>
					&nbsp;&nbsp;&nbsp;<input class="btn btn-primary btn-sm "
					value="&nbsp;&nbsp;&nbsp;查询&nbsp;&nbsp;&nbsp;" onclick="query()"
					type="submit" /></td></tr>
					<tr><td width="100px;">APP版本</td><td>
						<div>
						<label class="btn btn-info btn-sm">
		    				<input type="checkbox" id="appVersionAll" onclick="clickAll('${model.crashLogDisplayInfo.fieldsInfo.appVersions}', 'appVersion')" unchecked>All
		  				</label><c:forEach var="item" items="${model.crashLogDisplayInfo.fieldsInfo.appVersions}" varStatus="status"><label class="btn btn-info btn-sm"><input type="checkbox" id="appVersion_${item}" value="${item}" onclick="clickMe('${model.crashLogDisplayInfo.fieldsInfo.appVersions}', 'appVersion')" unchecked>${item}</label></c:forEach>
						</div>
						</td></tr>
					<tr><td width="60px;">平台版本</td><td><div><label class="btn btn-info btn-sm">
		    				<input type="checkbox" id="platformVersionAll" onclick="clickAll('${model.crashLogDisplayInfo.fieldsInfo.platVersions}', 'platformVersion')" unchecked>All
		  				</label><c:forEach var="item" items="${model.crashLogDisplayInfo.fieldsInfo.platVersions}" varStatus="status"><label class="btn btn-info btn-sm"><input type="checkbox" id="platformVersion_${item}" value="${item}" onclick="clickMe('${model.crashLogDisplayInfo.fieldsInfo.platVersions}', 'platformVersion')" unchecked>${item}</label></c:forEach>
						</div>
						</td></tr>
					<tr><td width="60px;"> 模块</td><td><div>
						<label class="btn btn-info btn-sm">
		    				<input type="checkbox" id="moduleAll" onclick="clickAll('${model.crashLogDisplayInfo.fieldsInfo.modules}', 'module')" unchecked>All
		  				</label><c:forEach var="item" items="${model.crashLogDisplayInfo.fieldsInfo.modules}" varStatus="status"><label class="btn btn-info btn-sm"><input type="checkbox" id="module_${item}" value="${item}" onclick="clickMe('${model.crashLogDisplayInfo.fieldsInfo.modules}', 'module')" unchecked>${item}</label></c:forEach>
						</div>
						</td></tr>
					<tr><td width="60px;"> 级别</td><td><div>
						<label class="btn btn-info btn-sm">
		    				<input type="checkbox" id="levelAll" onclick="clickAll('${model.crashLogDisplayInfo.fieldsInfo.levels}', 'level')"  unchecked>All
		  				</label><c:forEach var="item" items="${model.crashLogDisplayInfo.fieldsInfo.levels}" varStatus="status"><label class="btn btn-info btn-sm"><input type="checkbox" id="level_${item}" value="${item}" onclick="clickMe('${model.crashLogDisplayInfo.fieldsInfo.levels}', 'level')" unchecked>${item}</label></c:forEach>
						</div>
						</td></tr>
					<tr><td width="60px;">设备</td><td>
						<select multiple="true"	class="chosen-select tag-input-style" id="device_select" name="devices"  data-placeholder="Choose devices...">
						<option id="device_all" value="device_all">ALL</option>
						<c:forEach var="item" items="${model.crashLogDisplayInfo.fieldsInfo.devices}">
							<option id="${item}" value="${item}">${item}</option>
						</c:forEach>
						</select>
						</td></tr>
	</table>
	</div>
	<br>
	<table class="table table-hover table-striped table-condensed"  style="width:100%">
	<tr>
		<th width="30%">Msg</th>
		<th width="5%">Count</th>
		<th width="55%">SampleLinks</th>
	</tr>
	<tr>
		<td><strong>Total</strong></td>
		<td class="right">${w:format(model.crashLogDisplayInfo.totalCount,'#,###,###,###,##0')}&nbsp;</td>
		<td></td>
	</tr>
	<c:forEach var="error" items="${model.crashLogDisplayInfo.errors}" varStatus="index">
	<tr>
		<td>${error.msg}</td>
		<td  class="right">${w:format(error.count,'#,###,###,###,##0')}&nbsp;</td>
		<td >
			<c:forEach var="id" items="${error.ids}" varStatus="linkIndex">
				<a href="/cat/r/app?op=appCrashLogDetail&id=${id}">${linkIndex.first?'L':(linkIndex.last?'g':'o')}</a>
			</c:forEach>
		</td>
	</tr>
	</c:forEach>
</table>

</a:mobile>

<script type="text/javascript">
	function query(){
		var time = $("#time").val();
		var times = time.split(" ");
		var period = times[0];
		var start = converTimeFormat(times[1]);
		var end = converTimeFormat($("#time2").val());
		var dpid = $("#dpid").val();
		var appName = $("#appName").val();
		var platform = $("#platform").val();
		
 		var appVersion = queryField('${model.crashLogDisplayInfo.fieldsInfo.appVersions}','appVersion');
		var platVersion = queryField('${model.crashLogDisplayInfo.fieldsInfo.platVersions}','platformVersion');
		var module = queryField('${model.crashLogDisplayInfo.fieldsInfo.modules}','module');
		var level = queryField('${model.crashLogDisplayInfo.fieldsInfo.levels}','level');
		var device = queryDevice();
		var split = ";";
		var query = appVersion + split + platVersion + split + module + split + level + split + device;
 		
 		window.location.href = "?op=appCrashLog&crashLogQuery.day=" + period + "&crashLogQuery.startTime=" + start + "&crashLogQuery.endTime=" + end
 			 + "&crashLogQuery.appName=" + appName + "&crashLogQuery.platform=" + platform + "&crashLogQuery.dpid=" + dpid + "&crashLogQuery.query=" + query;
	}
	
	function queryDevice() {
		var device = "";
		$('.search-choice').each(function(){
			var o = $(this).children("span").eq(0).html();
			if (o == 'ALL') {
				device = "";
				return;
			} else {
				device += o + ":";
			}
			console.log(device);
		});
		return device;
	}
	
	$("#appName")
	  .change(function () {
			var time = $("#time").val();
			var times = time.split(" ");
			var period = times[0];
			var start = converTimeFormat(times[1]);
			var end = converTimeFormat($("#time2").val());
			var dpid = $("#dpid").val();
			var appName = $("#appName").val();
			var platform = $("#platform").val();
			
	 		window.location.href = "?op=appCrashLog&crashLogQuery.day=" + period + "&crashLogQuery.startTime=" + start + "&crashLogQuery.endTime=" + end
			 + "&crashLogQuery.appName=" + appName + "&crashLogQuery.platform=" + platform + "&crashLogQuery.dpid=" + dpid ;

	  })
	  
	$(document).ready(
		function() {
			$('#appCrashLog').addClass('active');
			$('#time').datetimepicker({
				format:'Y-m-d H:i',
				step:30,
				maxDate:0
			});
			$('#time2').datetimepicker({
				datepicker:false,
				format:'H:i',
				step:30,
				maxDate:0
			});
			
			var startTime = '${payload.crashLogQuery.startTime}';
			if (startTime == null || startTime.length == 0) {
				$("#time").val(getDate());
			} else {
				$("#time").val('${payload.crashLogQuery.day} ' + startTime);
			}
			
			var endTime = '${payload.crashLogQuery.endTime}';
			if (endTime == null || endTime.length == 0){
				$("#time2").val(getTime());
			}else{
				$("#time2").val(endTime);
			}
			
			var appName = '${payload.crashLogQuery.appName}';
			if (appName != null && appName.length != 0) {
				$("#appName").val(appName);
			}
			
			var platform = '${payload.crashLogQuery.platform}';
			if (platform != null && platform.length != 0) {
				$("#platform").val(platform);
			}
			
			var dpid = '${payload.crashLogQuery.dpid}';
			if (dpid != null && dpid.length != 0) {
				$("#dpid").val(dpid);
			}
			
			var fields = "${payload.crashLogQuery.query}".split(";");
			docReady(fields[0], '${model.crashLogDisplayInfo.fieldsInfo.appVersions}','appVersion');
			docReady(fields[1], '${model.crashLogDisplayInfo.fieldsInfo.platVersions}','platformVersion');
			docReady(fields[2], '${model.crashLogDisplayInfo.fieldsInfo.modules}','module');
			docReady(fields[3], '${model.crashLogDisplayInfo.fieldsInfo.levels}','level');
			
			$("#device_select").select({
				placeholder : "选择执行任务的设备",
				allowClear : true
			});
			
			if(typeof fields[4] == "undefined" || fields[4].length == 0){
				$('#device_all').attr("selected", "true");
			}else{
				urls = fields[4].split(":");
				for(var i=0; i<urls.length; i++) {
					var deviceid = urls[i];
					$('#' + deviceid).attr("selected", "true");
				}
			}
			
			$('.chosen-select').chosen({
				allow_single_deselect : true
			});
			//resize the chosen on window resize
			$(window).off('resize.chosen').on('resize.chosen', function() {
				$('.chosen-select').each(function() {
					var $this = $(this);
					$this.next().css({
						'width' : '800px'
					});
				})
			}).trigger('resize.chosen');
		});
	
	function docReady(field, fields, prefix){
		var urls = [];
		
		if(typeof field == "undefined" || field.length == 0){
			document.getElementById(prefix + "All").checked = true;
			clickAll(fields, prefix);
		}else{
			urls = field.split(":");
			for(var i=0; i<urls.length; i++) {
				if(document.getElementById(prefix + "_" + urls[i]) != null) {
					document.getElementById(prefix + "_" + urls[i]).checked = true;
				}
			}
		}
	}
	
	function getDate() {
		var myDate = new Date();
		var myMonth = new Number(myDate.getMonth());
		var month = myMonth + 1;
		var day = myDate.getDate();
		
		if(month<10){
			month = '0' + month;
		}
		if(day<10){
			day = '0' + day;
		}
		
		var myHour = new Number(myDate.getHours());
		
		if(myHour < 10){
			myHour = '0' + myHour;
		}
		
		return myDate.getFullYear() + "-" + month + "-" + day + " " + myHour + ":00";
	}

	function getTime(){
		var myDate = new Date();
		var myHour = new Number(myDate.getHours());
		var myMinute = new Number(myDate.getMinutes());
		
		if(myHour < 10){
			myHour = '0' + myHour;
		}
		if(myMinute < 10){
			myMinute = '0' + myMinute;
		}
		return myHour + ":" + myMinute;
	}

	function converTimeFormat(time){
		var times = time.split(":");
		var hour = times[0];
		var minute = times[1];
		
		if(hour.length == 1){
			hour = "0" + hour;
		}
		if(minute.length == 1) {
			minute = "0" + minute;
		}
		return hour + ":" + minute;
	}
	
	function clickMe(fields, prefix) {
		var fs = [];
		if(fields != "[]") {
			fs = fields.replace(/[\[\]]/g,'').split(', ');
		}
		
		var num = 0;
		for( var i=0; i<fs.length; i++){
		 	var f = prefix + "_" + fs[i];
			if(document.getElementById(f).checked){
				num ++;
			}else{
				document.getElementById(prefix + "All").checked = false;
				break;
			} 
		}
		if(num > 0 && num == fs.length) {
			document.getElementById(prefix + "All").checked = true;
		}
	}
	
	function clickAll(fields, prefix) {
		var fs = [];
		if(fields.length > 0){
			fs = fields.replace(/[\[\]]/g,'').split(', ');
			for( var i=0; i<fs.length; i++){
			 	var f = prefix + "_" + fs[i];
			 	if(document.getElementById(f) != undefined) {
					document.getElementById(f).checked = document.getElementById(prefix + "All").checked;
			 	}
			}
		}
	}
	
	function queryField(fields, prefix){
		var fs = [];
		if(fields.length > 0) {
			fs = fields.replace(/[\[\]]/g,'').split(', ');
		}
		
		var url = '';
		var num = 0;
		if(document.getElementById(prefix + "All").checked == false && fs.length > 0) {
			for( var i=0; i<fs.length; i++){
			 	var f = prefix + "_" + fs[i];
				if(document.getElementById(f) != undefined 
						&& document.getElementById(f).checked){
					url += fs[i] + ":";
				} 
			}
			url = url.substring(0, url.length-1);
		}else{
			url = "";
		}
		return url;
	}
	
</script>

<style type="text/css">
	.row-fluid .span2 {
		width:10%;
	}
	.row-fluid .span10 {
		width:87%;
	}
	.report .btn-group {
		position: relative;
		display: inline-block;
		font-size: 0;
		white-space: normal;
		vertical-align: middle;
	}
	.chosen-container-multi .chosen-choices li.search-choice .search-choice-close {
		background:inherit;
	}
</style>