$(function() {
	// initMenu();
	repaintMenu();
});
var menuData;
function repaintMenu() {
	$.getJSON("menu", function(data) {
		//$("#menu").empty();
		menuData=data;
		for(var i=0;i<data.length;i++){
			var n=data[i].name;
			if(n.length>24){
				n=n.substring(0,20)+"...";
			}
			var t="<a href=\"#\" class=\"list-group-item\" menuindex='"+i+"'>";
                t+="   <span class=\"glyphicon glyphicon-plus\">"+n+"</span><span class=\"badge\">"+data[i].items.length+"</span>";
                t+="</a>";
                t+="<div class=\"list-group hide\">";
                if(data[i].items.length>0){
                	for(var j=0;j<data[i].items.length;j++){
                        var m=data[i].items[j].name;
                        if(m.length>20){
                            m=m.substring(0,20)+"...";
                        }
                		t+="<button class=\"list-group-item\" menuindex='"+i+"' itemsindex='"+j+"'>"+m+"</button>";
                	}
                }
                t+="</div>";
			$("#menu").append(t);
		}
		initMenu();
	});
}
function initMenu() {
	$("button.list-group-item").each(function() {
		$(this).html("&emsp;&emsp;" + $(this).html());
	});
	$("div.list-group>a").click(
			function() {
				$("div.list-group>a").each(
						function() {
							if ($(this).next().hasClass("list-group")) {
								$(this).next().find("button.list-group-item")
										.removeClass("gray");
							}
						}).removeClass("gray");
				$(this).addClass("gray");
			});
	$("button.list-group-item").click(function() {
		$("button.list-group-item").removeClass("active");
		if (!$(this).parent().prev().hasClass("gray")) {
			$("div.list-group>a").removeClass("gray");
			$("a.list-group").removeClass("glyphicon-minus").addClass("glyphicon-plus");
			$(this).parent().prev().addClass("glyphicon-minus").addClass("glyphicon-plus").addClass("gray");
		}
		$(this).addClass("active");
		//重绘右边
		var mi=$(this).attr("menuindex");
		var ii=$(this).attr("itemsindex");
		var item=menuData[mi].items[ii];
		$("#header").html(menuData[mi].className+"."+item.methodName);
		$("#methodName").val(item.methodName);
		$("#beanName").val(item.beanName);
		$("#reqtbody").empty();
		for(var i=0;i<item.params.length;i++){
			var p=item.params[i];
			var tr="<tr><td>"+(i+1)+"</td><td>";
			if(p.type=="CLASS"){
				tr+="<a href='#' onclick=\"showc('"+p.c+"')\">"+p.name+"</a>";
			}else{
				tr+=p.name;
			}
			if(p.type=="NUM"){
				tr+="</td><td><input type='text' name='params["+i+"]' value='"+(p.defaultValue==null?"":p.defaultValue)+"'></td><td>"+p.desc+"</td>";									
			}else{
				tr+="</td><td><textarea rows='1' name='params["+i+"]'>"+(p.defaultValue==null?"":p.defaultValue)+"</textarea></td><td>"+p.desc+"</td>";				
			}
			$("#reqtbody").append(tr);
		}
		$("#resdesc").empty().html("<a class='btn' onclick='showc(\""+item.rc+"\")'>"+item.rdesc+"</a>");
	});
	$("a.list-group-item").click(function() {
		var a = $(this).find("span.glyphicon:eq(0)");
		var m_i=parseInt($(this).attr("menuindex"));
		$("#menu>a.list-group-item[menuindex!="+m_i+"]").find("span.glyphicon:eq(0)").removeClass("glyphicon-minus").addClass("glyphicon-plus");
		$("#menu>div.list-group").addClass("hide");
		if ($(a).hasClass("glyphicon-minus")) {
			$(a).removeClass("glyphicon-minus");
			$(a).addClass("glyphicon-plus");
			$(a).parent().next().addClass("hide");
		} else if ($(a).hasClass("glyphicon-plus")) {
			$(a).removeClass("glyphicon-plus");
			$(a).addClass("glyphicon-minus");
			$(a).parent().next().removeClass("hide");
		}
	});
}
function nul(exp){
	return !exp && typeof exp != "undefined" && exp != 0;
}
$(function() {
	$("input[type=datetime]").datetimepicker({
		clearBtn : 1,
		todayBtn : 1,
		autoclose : 1
	});
	$(".hide-desc").click(function() {
		var a = $(this).parent().parent().next();
		if ($(a).hasClass("hide")) {
			$(a).removeClass("hide");
		} else {
			$(a).addClass("hide");
		}
	});
})
$(function() {
	$('[data-toggle="popover"]').popover({
		'placement' : 'bottom'
	})
})
function furl(path) {
	var u = window.location.protocol + window.location.host
			+ (window.location.port == 80 || window.location.port == "") ? ""
			: (":" + window.location.port);
	if (u.startWith('/')) {
		return u + path;
	} else {
		return u + "/" + path;
	}
}
function showc(cname){
	$.post("link","className="+cname,function(data){
		if($(".modal").length>0){
			var d="<table id='mtable' class='table table-condensed'>";
			d+="<thead><tr><th>参数名</th><th>描述</th><th>默认值</th></tr></thead>";
			d+="<tbody>";
			for(var i=0;i<data.params.length;i++){
				var p=data.params[i];
				d+="<tr>";
				if(p.type=="CLASS"){
					d+="<td><a href='#' onclick=\"showc('"+p.c+"')\">"+p.name+"</a></td>";
				}else{
					d+="<td>"+p.name+"</td>";
				}
				d+="<td>"+p.desc+"</td>";
				d+="<td>"+p.defaultValue+"</td></tr>";
			}
			d+="</tbody>";
			d+="</table>";
			$("#mbody").empty().html(d);
			if(!$("#modal").hasClass("in")){
				$(".modal").modal();
			}
		}else{
			var d="<div class='modal fade' id='modal'>";
			d+="<div class='modal-dialog'>";
			d+="<div class='modal-content' id='mcontent'>";
			d+="<div class='modal-body' id='mbody'>";
			d+="<table id='mtable' class='table table-condensed'>";
			d+="<thead><tr><th>参数名</th><th>描述</th><th>默认值</th></tr></thead>";
			d+="<tbody>";
			for(var i=0;i<data.params.length;i++){
				var p=data.params[i];
				d+="<tr>";
				if(p.type=="CLASS"){
					d+="<td><a href='#' onclick=\"showc('"+p.c+"')\">"+p.name+"</a></td>";
				}else{
					d+="<td>"+p.name+"</td>";
				}
				d+="<td>"+p.desc+"</td>";
				d+="<td>"+p.defaultValue+"</td></tr>";
			}
			d+="</tbody>";
			d+="</table>";
			d+="</div>";
			d+="</div>";
			d+="</div>";
			d+="</div>";
			$("body").append(d);
			$(".modal").modal();
		}
	},'json');
}
function send(){
	$("#progress>div").addClass("active");
	$.post("service",$("form").serialize(),function(data,textStatus,jqXHR){
		$("#progress>div").removeClass("progress-bar-info").removeClass("progress-bar-danger");
		if(data.code==1){
			$("#progress>div").addClass("progress-bar-success");
			$("#exdesc").html(data.stack);	
		}else{						
			$("#exdesc").html(data.stack);	
			data.stack="";
			$("#progress>div").addClass("progress-bar-danger");	
		}
		$("#status").html(jqXHR.status);
		$("#resbody").text(JSON.stringify(data.data));
	},"json").done(function(){
		$("#progress>div").removeClass("active");
	}).fail(function(data){
		$("#progress>div").removeClass("active");
		$("#progress>div").removeClass("progress-bar-info").removeClass("progress-bar-success").addClass("progress-bar-danger");
		$("#status").html(data.status);
		$("#resbody").text(data.responseText);
	});
}
function togmenu(){
	if($("#right").hasClass("col-sm-9")){
		$("#left").addClass("hide");
		$("#right").removeClass("col-sm-9").addClass("col-sm-12");
	}else{
		$("#left").removeClass("hide");
		$("#right").removeClass("col-sm-12").addClass("col-sm-9");
	}
}