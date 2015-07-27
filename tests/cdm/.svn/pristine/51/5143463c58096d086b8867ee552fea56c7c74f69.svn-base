try {
AJS.Confluence.SharePage={};
AJS.Confluence.SharePage.autocompleteUser=function(C){C=C||document.body;
var D=AJS.$,A=/^([a-zA-Z0-9_\.\-\+\!#\$%&'\*/=\?\^_`{|}~])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
var B=function(F){if(!F||!F.result){throw new Error("Invalid JSON format")
}var E=[];
E.push(F.result);
return E
};
D("input.autocomplete-sharepage[data-autocomplete-user-bound!=true]",C).each(function(){var G=D(this).attr("data-autocomplete-sharepage-bound","true").attr("autocomplete","off");
var F=G.attr("data-max")||10,I=G.attr("data-alignment")||"left",H=G.attr("data-dropdown-target"),E=null;
if(H){E=D(H)
}else{E=D("<div></div>");
G.after(E)
}E.addClass("aui-dd-parent autocomplete");
G.quicksearch(AJS.REST.getBaseUrl()+"search/user.json",function(){G.trigger("open.autocomplete-sharepage")
},{makeParams:function(J){return{"max-results":F,query:J.replace("{|}","")}
},dropdownPlacement:function(J){E.append(J)
},makeRestMatrixFromData:B,addDropdownData:function(K){var J=D.trim(G.val());
if(A.test(J)){K.push([{name:J,email:J,href:"#",icon:AJS.Confluence.getContextPath()+"/images/icons/profilepics/anonymous.png"}])
}if(!K.length){var L=G.attr("data-none-message");
if(L){K.push([{name:L,className:"no-results",href:"#"}])
}}return K
},ajsDropDownOptions:{alignment:I,displayHandler:function(J){if(J.restObj&&J.restObj.username){return J.name+" ("+J.restObj.username+")"
}return J.name
},selectionHandler:function(L,K){if(K.find(".search-for").length){G.trigger("selected.autocomplete-sharepage",{searchFor:G.val()});
return 
}if(K.find(".no-results").length){this.hide();
L.preventDefault();
return 
}var J=D("span:eq(0)",K).data("properties");
if(!J.email){J=J.restObj
}G.trigger("selected.autocomplete-sharepage",{content:J});
this.hide();
L.preventDefault()
}}})
})
};
(function(A){jQuery.fn.extend({elastic:function(){var B=["paddingTop","paddingRight","paddingBottom","paddingLeft","fontSize","lineHeight","fontFamily","width","fontWeight","border-top-width","border-right-width","border-bottom-width","border-left-width","borderTopStyle","borderTopColor","borderRightStyle","borderRightColor","borderBottomStyle","borderBottomColor","borderLeftStyle","borderLeftColor"];
return this.each(function(){if(this.type!=="textarea"){return false
}var G=jQuery(this),C=jQuery("<div />").css({position:"absolute",display:"none","word-wrap":"break-word","white-space":"pre-wrap"}),I=parseInt(G.css("line-height"),10)||parseInt(G.css("font-size"),"10"),K=parseInt(G.css("height"),10)||I*3,J=parseInt(G.css("max-height"),10)||Number.MAX_VALUE,D=0;
if(J<0){J=Number.MAX_VALUE
}C.appendTo(G.parent());
var F=B.length;
while(F--){C.css(B[F].toString(),G.css(B[F].toString()))
}function H(){var M=Math.floor(parseInt(G.width(),10));
if(C.width()!==M){C.css({width:M+"px"});
E(true)
}}function L(M,O){var N=Math.floor(parseInt(M,10));
if(G.height()!==N){G.css({height:N+"px",overflow:O})
}}function E(P){var O=G.val().replace(/&/g,"&amp;").replace(/ {2}/g,"&nbsp;").replace(/<|>/g,"&gt;").replace(/\n/g,"<br />");
var M=C.html().replace(/<br>/ig,"<br />");
if(P||O+"&nbsp;"!==M){C.html(O+"&nbsp;");
if(Math.abs(C.height()+I-G.height())>3){var N=C.height()+I;
if(N>=J){L(J,"auto")
}else{if(N<=K){L(K,"hidden")
}else{L(N,"hidden")
}}}}}G.css({overflow:"hidden"});
G.bind("keyup change cut paste",function(){E()
});
A(window).bind("resize",H);
G.bind("resize",H);
G.bind("update",E);
G.bind("input paste",function(M){setTimeout(E,250)
});
E()
})
}})
})(AJS.$);
(function(E){var D,B={hideCallback:A,width:280,offsetY:17,offsetX:-40,hideDelay:3600000};
var A=function(){E(".dashboard-actions .explanation").hide()
};
var C=function(I,G,H){I.empty();
I.append(AJS.template.load("share-content-popup").fill());
AJS.Confluence.SharePage.autocompleteUser();
var J=function(){D.hide();
return false
};
E(document).keyup(function(L){if(L.keyCode==27){J();
E(document).unbind("keyup",arguments.callee);
return false
}return true
});
I.find(".close-dialog").click(J);
I.find("#note").elastic();
I.find("form").submit(function(){var P=[];
I.find(".recipients li").each(function(Q,R){P.push(E(R).attr("data-username"))
});
if(P.length<=0){return false
}E("button,input,textarea",this).attr("disabled","disabled");
I.find(".progress-messages").text("Sending");
var M=Raphael.spinner(I.find(".progress-messages-icon")[0],7,"#666");
I.find(".progress-messages-icon").css("left","10px").css("position","absolute");
I.find(".progress-messages").css("padding-left",I.find(".progress-messages-icon").innerWidth()+5);
var P=[];
I.find(".recipients li[data-username]").each(function(Q,R){P.push(E(R).attr("data-username"))
});
var O=[];
I.find(".recipients li[data-email]").each(function(Q,R){O.push(E(R).attr("data-email"))
});
var L=I.find("#note");
var N={users:P,emails:O,note:L.hasClass("placeholded")?"":L.val(),entityId:AJS.params.pageId};
E.ajax({type:"POST",contentType:"application/json; charset=utf-8",url:AJS.Confluence.getContextPath()+"/rest/share-page/latest/share",data:JSON.stringify(N),dataType:"text",success:function(){setTimeout(function(){M();
I.find(".progress-messages-icon").css("width","17px");
I.find(".progress-messages-icon").css("height","17px");
I.find(".progress-messages-icon").addClass("done");
I.find(".progress-messages").text("Sent");
setTimeout(function(){J()
},1000)
},500)
},error:function(R,Q){M();
I.find(".progress-messages-icon").css("width","17px");
I.find(".progress-messages-icon").css("height","17px");
I.find(".progress-messages-icon").addClass("error");
I.find(".progress-messages").text("Error while sending")
}});
return false
});
var K=I.find("#users");
var F=I.find(".button-panel input");
K.bind("selected.autocomplete-sharepage",function(M,L){var N=function(P,Q){var S=I.find(".recipients"),R,O;
R="li[data-"+P+'="'+Q.content[P]+'"]';
if(S.find(R).length>0){S.find(R).hide()
}else{S.append(AJS.template.load("share-content-popup-recipient-"+P).fill(Q.content))
}O=S.find(R);
O.find(".remove-recipient").click(function(){O.remove();
if(S.find("li").length==0){F.attr("disabled","true")
}D.refresh();
K.focus();
return false
});
O.fadeIn(200)
};
if(L.content.email){N("email",L)
}else{N("username",L)
}D.refresh();
F.removeAttr("disabled");
K.val("");
return false
});
K.bind("open.autocomplete-sharepage",function(M,L){if(E("a:not(.no-results)",AJS.dropDown.current.links).length>0){AJS.dropDown.current.moveDown()
}});
K.keypress(function(L){if(L.keyCode==13){return false
}return true
});
E(document).bind("showLayer",function(N,M,L){if(M=="inlineDialog"&&L.popup==D){L.popup.find("#users").focus()
}});
E("#shareContentLink").parents().filter(function(){return this.scrollTop>0
}).scrollTop(0);
H()
};
AJS.toInit(function(F){D=AJS.InlineDialog(F("#shareContentLink"),"shareContentPopup",C,B)
})
})(AJS.$);
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (45648)");
        console.log(err);
    }
}

try {
AJS.toInit(function(A){if(A("#action-view-storage-link").length){A("#action-source-editor-view-storage-link").closest("li").hide()
}});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (44357)");
        console.log(err);
    }
}

try {
var Lockpoint={contextPath:"",i18n:{},innerAjaxFileAction:function(B,A,D){var C;
jQuery.ajax({url:Lockpoint.contextPath+"/lockpoint/ajaxhelper.action",type:"POST",dataType:"json",async:false,data:({gliffyObjects:[B+":"+A+":"+D]}),success:function(E){if(E.attachments&&E.attachments.length>0){C=E.attachments[0]
}}});
return C
},performAjaxFileAction:function(B,A,D){var C=Lockpoint.innerAjaxFileAction(B,A,D);
if(typeof C=="undefined"){return"Server error"
}else{if(C.success){return""
}else{return C.errorMessage
}}},performRestLockOrUnlock:function(B,A){jQuery.ajax({url:Lockpoint.contextPath+"/rest/lockpoint/1.0/"+B,dataType:"json",contentType:"application/json; charset=UTF-8",async:true,context:A.opts,data:JSON.stringify([{ceoId:A.ceoId,filename:A.filename,lockMode:A.lockMode}]),type:"POST",success:function(C){if(C[0]&&C[0].success){A.success(C[0],this)
}else{A.failure((C&&C[0])?C[0]:null,this)
}},error:function(C){A.failure(null,this)
}})
},performRestLock:function(A){Lockpoint.performRestLockOrUnlock("lock",A)
},performRestUnlock:function(B,A){Lockpoint.performRestLockOrUnlock("unlock",B)
}};
jQuery(function(A){Lockpoint.contextPath=(jQuery("#confluence-context-path").attr("content")||"")
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (22846)");
        console.log(err);
    }
}

try {
/*
 * jquery.qtip. The jQuery tooltip plugin
 *
 * Copyright (c) 2009 Craig Thompson
 * http://craigsworks.com
 *
 * Licensed under MIT
 * http://www.opensource.org/licenses/mit-license.php
 *
 * Launch  : February 2009
 * Version : 1.0.0-rc3
 * Released: Tuesday 12th May, 2009 - 00:00
 * Debug: jquery.qtip.debug.js
 */
(function(F){F.fn.qtip=function(a,T){var X,S,Z,R,W,V,U,Y;
if(typeof a=="string"){if(typeof F(this).data("qtip")!=="object"){F.fn.qtip.log.error.call(self,1,F.fn.qtip.constants.NO_TOOLTIP_PRESENT,false)
}if(a=="api"){return F(this).data("qtip").interfaces[F(this).data("qtip").current]
}else{if(a=="interfaces"){return F(this).data("qtip").interfaces
}}}else{if(!a){a={}
}if(typeof a.content!=="object"||(a.content.jquery&&a.content.length>0)){a.content={text:a.content}
}if(typeof a.content.title!=="object"){a.content.title={text:a.content.title}
}if(typeof a.position!=="object"){a.position={corner:a.position}
}if(typeof a.position.corner!=="object"){a.position.corner={target:a.position.corner,tooltip:a.position.corner}
}if(typeof a.show!=="object"){a.show={when:a.show}
}if(typeof a.show.when!=="object"){a.show.when={event:a.show.when}
}if(typeof a.show.effect!=="object"){a.show.effect={type:a.show.effect}
}if(typeof a.hide!=="object"){a.hide={when:a.hide}
}if(typeof a.hide.when!=="object"){a.hide.when={event:a.hide.when}
}if(typeof a.hide.effect!=="object"){a.hide.effect={type:a.hide.effect}
}if(typeof a.style!=="object"){a.style={name:a.style}
}a.style=C(a.style);
R=F.extend(true,{},F.fn.qtip.defaults,a);
R.style=A.call({options:R},R.style);
R.user=F.extend(true,{},a)
}return F(this).each(function(){if(typeof a=="string"){V=a.toLowerCase();
Z=F(this).qtip("interfaces");
if(typeof Z=="object"){if(T===true&&V=="destroy"){while(Z.length>0){Z[Z.length-1].destroy()
}}else{if(T!==true){Z=[F(this).qtip("api")]
}for(X=0;
X<Z.length;
X++){if(V=="destroy"){Z[X].destroy()
}else{if(Z[X].status.rendered===true){if(V=="show"){Z[X].show()
}else{if(V=="hide"){Z[X].hide()
}else{if(V=="focus"){Z[X].focus()
}else{if(V=="disable"){Z[X].disable(true)
}else{if(V=="enable"){Z[X].disable(false)
}}}}}}}}}}}else{U=F.extend(true,{},R);
U.hide.effect.length=R.hide.effect.length;
U.show.effect.length=R.show.effect.length;
if(U.position.container===false){U.position.container=F(document.body)
}if(U.position.target===false){U.position.target=F(this)
}if(U.show.when.target===false){U.show.when.target=F(this)
}if(U.hide.when.target===false){U.hide.when.target=F(this)
}S=F.fn.qtip.interfaces.length;
for(X=0;
X<S;
X++){if(typeof F.fn.qtip.interfaces[X]=="undefined"){S=X;
break
}}W=new D(F(this),U,S);
F.fn.qtip.interfaces[S]=W;
if(F(this).data("qtip")!=null&&typeof F(this).data("qtip")=="object"){if(typeof F(this).attr("qtip")==="undefined"){F(this).data("qtip").current=F(this).data("qtip").interfaces.length
}F(this).data("qtip").interfaces.push(W)
}else{F(this).data("qtip",{current:0,interfaces:[W]})
}if(U.content.prerender===false&&U.show.when.event!==false&&U.show.ready!==true){U.show.when.target.bind(U.show.when.event+".qtip-"+S+"-create",{qtip:S},function(b){Y=F.fn.qtip.interfaces[b.data.qtip];
Y.options.show.when.target.unbind(Y.options.show.when.event+".qtip-"+b.data.qtip+"-create");
Y.cache.mouse={x:b.pageX,y:b.pageY};
O.call(Y);
Y.options.show.when.target.trigger(Y.options.show.when.event)
})
}else{W.cache.mouse={x:U.show.when.target.offset().left,y:U.show.when.target.offset().top};
O.call(W)
}}})
};
function D(T,S,U){var R=this;
R.id=U;
R.options=S;
R.status={animated:false,rendered:false,disabled:false,focused:false};
R.elements={target:T.addClass(R.options.style.classes.target),tooltip:null,wrapper:null,content:null,contentWrapper:null,title:null,button:null,tip:null,bgiframe:null};
R.cache={mouse:{},position:{},toggle:0};
R.timers={};
F.extend(R,R.options.api,{show:function(X){var W,Y;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"show")
}if(R.elements.tooltip.css("display")!=="none"){return R
}R.elements.tooltip.stop(true,false);
W=R.beforeShow.call(R,X);
if(W===false){return R
}function V(){if(R.options.position.type!=="static"){R.focus()
}R.onShow.call(R,X);
if(F.browser.msie){R.elements.tooltip.get(0).style.removeAttribute("filter")
}}R.cache.toggle=1;
if(R.options.position.type!=="static"){R.updatePosition(X,(R.options.show.effect.length>0))
}if(typeof R.options.show.solo=="object"){Y=F(R.options.show.solo)
}else{if(R.options.show.solo===true){Y=F("div.qtip").not(R.elements.tooltip)
}}if(Y){Y.each(function(){if(F(this).qtip("api").status.rendered===true){F(this).qtip("api").hide()
}})
}if(typeof R.options.show.effect.type=="function"){R.options.show.effect.type.call(R.elements.tooltip,R.options.show.effect.length);
R.elements.tooltip.queue(function(){V();
F(this).dequeue()
})
}else{switch(R.options.show.effect.type.toLowerCase()){case"fade":R.elements.tooltip.fadeIn(R.options.show.effect.length,V);
break;
case"slide":R.elements.tooltip.slideDown(R.options.show.effect.length,function(){V();
if(R.options.position.type!=="static"){R.updatePosition(X,true)
}});
break;
case"grow":R.elements.tooltip.show(R.options.show.effect.length,V);
break;
default:R.elements.tooltip.show(null,V);
break
}R.elements.tooltip.addClass(R.options.style.classes.active)
}return F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_SHOWN,"show")
},hide:function(X){var W;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"hide")
}else{if(R.elements.tooltip.css("display")==="none"){return R
}}clearTimeout(R.timers.show);
R.elements.tooltip.stop(true,false);
W=R.beforeHide.call(R,X);
if(W===false){return R
}function V(){R.onHide.call(R,X)
}R.cache.toggle=0;
if(typeof R.options.hide.effect.type=="function"){R.options.hide.effect.type.call(R.elements.tooltip,R.options.hide.effect.length);
R.elements.tooltip.queue(function(){V();
F(this).dequeue()
})
}else{switch(R.options.hide.effect.type.toLowerCase()){case"fade":R.elements.tooltip.fadeOut(R.options.hide.effect.length,V);
break;
case"slide":R.elements.tooltip.slideUp(R.options.hide.effect.length,V);
break;
case"grow":R.elements.tooltip.hide(R.options.hide.effect.length,V);
break;
default:R.elements.tooltip.hide(null,V);
break
}R.elements.tooltip.removeClass(R.options.style.classes.active)
}return F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_HIDDEN,"hide")
},updatePosition:function(V,W){var b,f,l,j,g,d,X,h,a,c,k,Z,e,Y;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"updatePosition")
}else{if(R.options.position.type=="static"){return F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.CANNOT_POSITION_STATIC,"updatePosition")
}}f={position:{left:0,top:0},dimensions:{height:0,width:0},corner:R.options.position.corner.target};
l={position:R.getPosition(),dimensions:R.getDimensions(),corner:R.options.position.corner.tooltip};
if(R.options.position.target!=="mouse"){if(R.options.position.target.get(0).nodeName.toLowerCase()=="area"){j=R.options.position.target.attr("coords").split(",");
for(b=0;
b<j.length;
b++){j[b]=parseInt(j[b])
}g=R.options.position.target.parent("map").attr("name");
d=F('img[usemap="#'+g+'"]:first').offset();
f.position={left:Math.floor(d.left+j[0]),top:Math.floor(d.top+j[1])};
switch(R.options.position.target.attr("shape").toLowerCase()){case"rect":f.dimensions={width:Math.ceil(Math.abs(j[2]-j[0])),height:Math.ceil(Math.abs(j[3]-j[1]))};
break;
case"circle":f.dimensions={width:j[2]+1,height:j[2]+1};
break;
case"poly":f.dimensions={width:j[0],height:j[1]};
for(b=0;
b<j.length;
b++){if(b%2==0){if(j[b]>f.dimensions.width){f.dimensions.width=j[b]
}if(j[b]<j[0]){f.position.left=Math.floor(d.left+j[b])
}}else{if(j[b]>f.dimensions.height){f.dimensions.height=j[b]
}if(j[b]<j[1]){f.position.top=Math.floor(d.top+j[b])
}}}f.dimensions.width=f.dimensions.width-(f.position.left-d.left);
f.dimensions.height=f.dimensions.height-(f.position.top-d.top);
break;
default:return F.fn.qtip.log.error.call(R,4,F.fn.qtip.constants.INVALID_AREA_SHAPE,"updatePosition");
break
}f.dimensions.width-=2;
f.dimensions.height-=2
}else{if(R.options.position.target.add(document.body).length===1){f.position={left:F(document).scrollLeft(),top:F(document).scrollTop()};
f.dimensions={height:F(window).height(),width:F(window).width()}
}else{if(typeof R.options.position.target.attr("qtip")!=="undefined"){f.position=R.options.position.target.qtip("api").cache.position
}else{f.position=R.options.position.target.offset()
}f.dimensions={height:R.options.position.target.outerHeight(),width:R.options.position.target.outerWidth()}
}}X=F.extend({},f.position);
if(f.corner.search(/right/i)!==-1){X.left+=f.dimensions.width
}if(f.corner.search(/bottom/i)!==-1){X.top+=f.dimensions.height
}if(f.corner.search(/((top|bottom)Middle)|center/)!==-1){X.left+=(f.dimensions.width/2)
}if(f.corner.search(/((left|right)Middle)|center/)!==-1){X.top+=(f.dimensions.height/2)
}}else{f.position=X={left:R.cache.mouse.x,top:R.cache.mouse.y};
f.dimensions={height:1,width:1}
}if(l.corner.search(/right/i)!==-1){X.left-=l.dimensions.width
}if(l.corner.search(/bottom/i)!==-1){X.top-=l.dimensions.height
}if(l.corner.search(/((top|bottom)Middle)|center/)!==-1){X.left-=(l.dimensions.width/2)
}if(l.corner.search(/((left|right)Middle)|center/)!==-1){X.top-=(l.dimensions.height/2)
}h=(F.browser.msie)?1:0;
a=(F.browser.msie&&parseInt(F.browser.version.charAt(0))===6)?1:0;
if(R.options.style.border.radius>0){if(l.corner.search(/Left/)!==-1){X.left-=R.options.style.border.radius
}else{if(l.corner.search(/Right/)!==-1){X.left+=R.options.style.border.radius
}}if(l.corner.search(/Top/)!==-1){X.top-=R.options.style.border.radius
}else{if(l.corner.search(/Bottom/)!==-1){X.top+=R.options.style.border.radius
}}}if(h){if(l.corner.search(/top/)!==-1){X.top-=h
}else{if(l.corner.search(/bottom/)!==-1){X.top+=h
}}if(l.corner.search(/left/)!==-1){X.left-=h
}else{if(l.corner.search(/right/)!==-1){X.left+=h
}}if(l.corner.search(/leftMiddle|rightMiddle/)!==-1){X.top-=1
}}if(R.options.position.adjust.screen===true){X=N.call(R,X,f,l)
}if(R.options.position.target==="mouse"&&R.options.position.adjust.mouse===true){if(R.options.position.adjust.screen===true&&R.elements.tip){k=R.elements.tip.attr("rel")
}else{k=R.options.position.corner.tooltip
}X.left+=(k.search(/right/i)!==-1)?-6:6;
X.top+=(k.search(/bottom/i)!==-1)?-6:6
}if(!R.elements.bgiframe&&F.browser.msie&&parseInt(F.browser.version.charAt(0))==6){F("select, object").each(function(){Z=F(this).offset();
Z.bottom=Z.top+F(this).height();
Z.right=Z.left+F(this).width();
if(X.top+l.dimensions.height>=Z.top&&X.left+l.dimensions.width>=Z.left){J.call(R)
}})
}X.left+=R.options.position.adjust.x;
X.top+=R.options.position.adjust.y;
e=R.getPosition();
if(X.left!=e.left||X.top!=e.top){Y=R.beforePositionUpdate.call(R,V);
if(Y===false){return R
}R.cache.position=X;
if(W===true){R.status.animated=true;
R.elements.tooltip.animate(X,200,"swing",function(){R.status.animated=false
})
}else{R.elements.tooltip.css(X)
}R.onPositionUpdate.call(R,V);
if(typeof V!=="undefined"&&V.type&&V.type!=="mousemove"){F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_POSITION_UPDATED,"updatePosition")
}}return R
},updateWidth:function(V){var W;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"updateWidth")
}else{if(V&&typeof V!=="number"){return F.fn.qtip.log.error.call(R,2,"newWidth must be of type number","updateWidth")
}}W=R.elements.contentWrapper.siblings().add(R.elements.tip).add(R.elements.button);
if(!V){if(typeof R.options.style.width.value=="number"){V=R.options.style.width.value
}else{R.elements.tooltip.css({width:"auto"});
W.hide();
if(F.browser.msie){R.elements.wrapper.add(R.elements.contentWrapper.children()).css({zoom:"normal"})
}V=R.getDimensions().width+1;
if(!R.options.style.width.value){if(V>R.options.style.width.max){V=R.options.style.width.max
}if(V<R.options.style.width.min){V=R.options.style.width.min
}}}}if(V%2!==0){V-=1
}R.elements.tooltip.width(V);
W.show();
if(R.options.style.border.radius){R.elements.tooltip.find(".qtip-betweenCorners").each(function(X){F(this).width(V-(R.options.style.border.radius*2))
})
}if(F.browser.msie){R.elements.wrapper.add(R.elements.contentWrapper.children()).css({zoom:"1"});
R.elements.wrapper.width(V);
if(R.elements.bgiframe){R.elements.bgiframe.width(V).height(R.getDimensions.height)
}}return F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_WIDTH_UPDATED,"updateWidth")
},updateStyle:function(V){var Y,Z,W,X,a;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"updateStyle")
}else{if(typeof V!=="string"||!F.fn.qtip.styles[V]){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.STYLE_NOT_DEFINED,"updateStyle")
}}R.options.style=A.call(R,F.fn.qtip.styles[V],R.options.user.style);
R.elements.content.css(P(R.options.style));
if(R.options.content.title.text!==false){R.elements.title.css(P(R.options.style.title,true))
}R.elements.contentWrapper.css({borderColor:R.options.style.border.color});
if(R.options.style.tip.corner!==false){if(F("<canvas>").get(0).getContext){Y=R.elements.tooltip.find(".qtip-tip canvas:first");
W=Y.get(0).getContext("2d");
W.clearRect(0,0,300,300);
X=Y.parent("div[rel]:first").attr("rel");
a=B(X,R.options.style.tip.size.width,R.options.style.tip.size.height);
H.call(R,Y,a,R.options.style.tip.color||R.options.style.border.color)
}else{if(F.browser.msie){Y=R.elements.tooltip.find('.qtip-tip [nodeName="shape"]');
Y.attr("fillcolor",R.options.style.tip.color||R.options.style.border.color)
}}}if(R.options.style.border.radius>0){R.elements.tooltip.find(".qtip-betweenCorners").css({backgroundColor:R.options.style.border.color});
if(F("<canvas>").get(0).getContext){Z=G(R.options.style.border.radius);
R.elements.tooltip.find(".qtip-wrapper canvas").each(function(){W=F(this).get(0).getContext("2d");
W.clearRect(0,0,300,300);
X=F(this).parent("div[rel]:first").attr("rel");
Q.call(R,F(this),Z[X],R.options.style.border.radius,R.options.style.border.color)
})
}else{if(F.browser.msie){R.elements.tooltip.find('.qtip-wrapper [nodeName="arc"]').each(function(){F(this).attr("fillcolor",R.options.style.border.color)
})
}}}return F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_STYLE_UPDATED,"updateStyle")
},updateContent:function(Z,X){var Y,W,V;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"updateContent")
}else{if(!Z){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.NO_CONTENT_PROVIDED,"updateContent")
}}Y=R.beforeContentUpdate.call(R,Z);
if(typeof Y=="string"){Z=Y
}else{if(Y===false){return 
}}if(F.browser.msie){R.elements.contentWrapper.children().css({zoom:"normal"})
}if(Z.jquery&&Z.length>0){Z.clone(true).appendTo(R.elements.content).show()
}else{R.elements.content.html(Z)
}W=R.elements.content.find("img[complete=false]");
if(W.length>0){V=0;
W.each(function(b){F('<img src="'+F(this).attr("src")+'" />').load(function(){if(++V==W.length){a()
}})
})
}else{a()
}function a(){R.updateWidth();
if(X!==false){if(R.options.position.type!=="static"){R.updatePosition(R.elements.tooltip.is(":visible"),true)
}if(R.options.style.tip.corner!==false){M.call(R)
}}}R.onContentUpdate.call(R);
return F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_CONTENT_UPDATED,"loadContent")
},loadContent:function(V,Y,Z){var X;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"loadContent")
}X=R.beforeContentLoad.call(R);
if(X===false){return R
}if(Z=="post"){F.post(V,Y,W)
}else{F.get(V,Y,W)
}function W(a){R.onContentLoad.call(R);
F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_CONTENT_LOADED,"loadContent");
R.updateContent(a)
}return R
},updateTitle:function(V){if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"updateTitle")
}else{if(!V){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.NO_CONTENT_PROVIDED,"updateTitle")
}}returned=R.beforeTitleUpdate.call(R);
if(returned===false){return R
}if(R.elements.button){R.elements.button=R.elements.button.clone(true)
}R.elements.title.html(V);
if(R.elements.button){R.elements.title.prepend(R.elements.button)
}R.onTitleUpdate.call(R);
return F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_TITLE_UPDATED,"updateTitle")
},focus:function(Z){var X,W,V,Y;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"focus")
}else{if(R.options.position.type=="static"){return F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.CANNOT_FOCUS_STATIC,"focus")
}}X=parseInt(R.elements.tooltip.css("z-index"));
W=6000+F("div.qtip[qtip]").length-1;
if(!R.status.focused&&X!==W){Y=R.beforeFocus.call(R,Z);
if(Y===false){return R
}F("div.qtip[qtip]").not(R.elements.tooltip).each(function(){if(F(this).qtip("api").status.rendered===true){V=parseInt(F(this).css("z-index"));
if(typeof V=="number"&&V>-1){F(this).css({zIndex:parseInt(F(this).css("z-index"))-1})
}F(this).qtip("api").status.focused=false
}});
R.elements.tooltip.css({zIndex:W});
R.status.focused=true;
R.onFocus.call(R,Z);
F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_FOCUSED,"focus")
}return R
},disable:function(V){if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"disable")
}if(V){if(!R.status.disabled){R.status.disabled=true;
F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_DISABLED,"disable")
}else{F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.TOOLTIP_ALREADY_DISABLED,"disable")
}}else{if(R.status.disabled){R.status.disabled=false;
F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_ENABLED,"disable")
}else{F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.TOOLTIP_ALREADY_ENABLED,"disable")
}}return R
},destroy:function(){var V,W,X;
W=R.beforeDestroy.call(R);
if(W===false){return R
}if(R.status.rendered){R.options.show.when.target.unbind("mousemove.qtip",R.updatePosition);
R.options.show.when.target.unbind("mouseout.qtip",R.hide);
R.options.show.when.target.unbind(R.options.show.when.event+".qtip");
R.options.hide.when.target.unbind(R.options.hide.when.event+".qtip");
R.elements.tooltip.unbind(R.options.hide.when.event+".qtip");
R.elements.tooltip.unbind("mouseover.qtip",R.focus);
R.elements.tooltip.remove()
}else{R.options.show.when.target.unbind(R.options.show.when.event+".qtip-create")
}if(typeof R.elements.target.data("qtip")=="object"){X=R.elements.target.data("qtip").interfaces;
if(typeof X=="object"&&X.length>0){for(V=0;
V<X.length-1;
V++){if(X[V].id==R.id){X.splice(V,1)
}}}}delete F.fn.qtip.interfaces[R.id];
if(typeof X=="object"&&X.length>0){R.elements.target.data("qtip").current=X.length-1
}else{R.elements.target.removeData("qtip")
}R.onDestroy.call(R);
F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_DESTROYED,"destroy");
return R.elements.target
},getPosition:function(){var V,W;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"getPosition")
}V=(R.elements.tooltip.css("display")!=="none")?false:true;
if(V){R.elements.tooltip.css({visiblity:"hidden"}).show()
}W=R.elements.tooltip.offset();
if(V){R.elements.tooltip.css({visiblity:"visible"}).hide()
}return W
},getDimensions:function(){var V,W;
if(!R.status.rendered){return F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.TOOLTIP_NOT_RENDERED,"getDimensions")
}V=(!R.elements.tooltip.is(":visible"))?true:false;
if(V){R.elements.tooltip.css({visiblity:"hidden"}).show()
}W={height:R.elements.tooltip.outerHeight(),width:R.elements.tooltip.outerWidth()};
if(V){R.elements.tooltip.css({visiblity:"visible"}).hide()
}return W
}})
}function O(){var R,V,T,S,U,X,W;
R=this;
R.beforeRender.call(R);
R.status.rendered=true;
R.elements.tooltip='<div qtip="'+R.id+'" class="qtip '+(R.options.style.classes.tooltip||R.options.style)+'"style="display:none; -moz-border-radius:0; -webkit-border-radius:0; border-radius:0;position:'+R.options.position.type+';">  <div class="qtip-wrapper" style="position:relative; overflow:hidden; text-align:left;">    <div class="qtip-contentWrapper" style="overflow:hidden;">       <div class="qtip-content '+R.options.style.classes.content+'"></div></div></div></div>';
R.elements.tooltip=F(R.elements.tooltip);
R.elements.tooltip.appendTo(R.options.position.container);
R.elements.tooltip.data("qtip",{current:0,interfaces:[R]});
R.elements.wrapper=R.elements.tooltip.children("div:first");
R.elements.contentWrapper=R.elements.wrapper.children("div:first").css({background:R.options.style.background});
R.elements.content=R.elements.contentWrapper.children("div:first").css(P(R.options.style));
if(F.browser.msie){R.elements.wrapper.add(R.elements.content).css({zoom:1})
}if(R.options.hide.when.event=="unfocus"){R.elements.tooltip.attr("unfocus",true)
}if(typeof R.options.style.width.value=="number"){R.updateWidth()
}if(F("<canvas>").get(0).getContext||F.browser.msie){if(R.options.style.border.radius>0){L.call(R)
}else{R.elements.contentWrapper.css({border:R.options.style.border.width+"px solid "+R.options.style.border.color})
}if(R.options.style.tip.corner!==false){E.call(R)
}}else{R.elements.contentWrapper.css({border:R.options.style.border.width+"px solid "+R.options.style.border.color});
R.options.style.border.radius=0;
R.options.style.tip.corner=false;
F.fn.qtip.log.error.call(R,2,F.fn.qtip.constants.CANVAS_VML_NOT_SUPPORTED,"render")
}if((typeof R.options.content.text=="string"&&R.options.content.text.length>0)||(R.options.content.text.jquery&&R.options.content.text.length>0)){T=R.options.content.text
}else{if(typeof R.elements.target.attr("title")=="string"&&R.elements.target.attr("title").length>0){T=R.elements.target.attr("title").replace("\\n","<br />");
R.elements.target.attr("title","")
}else{if(typeof R.elements.target.attr("alt")=="string"&&R.elements.target.attr("alt").length>0){T=R.elements.target.attr("alt").replace("\\n","<br />");
R.elements.target.attr("alt","")
}else{T=" ";
F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.NO_VALID_CONTENT,"render")
}}}if(R.options.content.title.text!==false){I.call(R)
}R.updateContent(T);
K.call(R);
if(R.options.show.ready===true){R.show()
}if(R.options.content.url!==false){S=R.options.content.url;
U=R.options.content.data;
X=R.options.content.method||"get";
R.loadContent(S,U,X)
}R.onRender.call(R);
F.fn.qtip.log.error.call(R,1,F.fn.qtip.constants.EVENT_RENDERED,"render")
}function L(){var e,Y,S,a,W,d,T,f,c,X,V,b,Z,R,U;
e=this;
e.elements.wrapper.find(".qtip-borderBottom, .qtip-borderTop").remove();
S=e.options.style.border.width;
a=e.options.style.border.radius;
W=e.options.style.border.color||e.options.style.tip.color;
d=G(a);
T={};
for(Y in d){T[Y]='<div rel="'+Y+'" style="'+((Y.search(/Left/)!==-1)?"left":"right")+":0; position:absolute; height:"+a+"px; width:"+a+'px; overflow:hidden; line-height:0.1px; font-size:1px">';
if(F("<canvas>").get(0).getContext){T[Y]+='<canvas height="'+a+'" width="'+a+'" style="vertical-align: top"></canvas>'
}else{if(F.browser.msie){f=a*2+3;
T[Y]+='<v:arc stroked="false" fillcolor="'+W+'" startangle="'+d[Y][0]+'" endangle="'+d[Y][1]+'" style="width:'+f+"px; height:"+f+"px; margin-top:"+((Y.search(/bottom/)!==-1)?-2:-1)+"px; margin-left:"+((Y.search(/Right/)!==-1)?d[Y][2]-3.5:-1)+'px; vertical-align:top; display:inline-block; behavior:url(#default#VML)"></v:arc>'
}}T[Y]+="</div>"
}c=e.getDimensions().width-(Math.max(S,a)*2);
X='<div class="qtip-betweenCorners" style="height:'+a+"px; width:"+c+"px; overflow:hidden; background-color:"+W+'; line-height:0.1px; font-size:1px;">';
V='<div class="qtip-borderTop" dir="ltr" style="height:'+a+"px; margin-left:"+a+'px; line-height:0.1px; font-size:1px; padding:0;">'+T.topLeft+T.topRight+X;
e.elements.wrapper.prepend(V);
b='<div class="qtip-borderBottom" dir="ltr" style="height:'+a+"px; margin-left:"+a+'px; line-height:0.1px; font-size:1px; padding:0;">'+T.bottomLeft+T.bottomRight+X;
e.elements.wrapper.append(b);
if(F("<canvas>").get(0).getContext){e.elements.wrapper.find("canvas").each(function(){Z=d[F(this).parent("[rel]:first").attr("rel")];
Q.call(e,F(this),Z,a,W)
})
}else{if(F.browser.msie){e.elements.tooltip.append('<v:image style="behavior:url(#default#VML);"></v:image>')
}}R=Math.max(a,(a+(S-a)));
U=Math.max(S-a,0);
e.elements.contentWrapper.css({border:"0px solid "+W,borderWidth:U+"px "+R+"px"})
}function Q(T,V,R,S){var U=T.get(0).getContext("2d");
U.fillStyle=S;
U.beginPath();
U.arc(V[0],V[1],R,0,Math.PI*2,false);
U.fill()
}function E(U){var S,R,W,T,V;
S=this;
if(S.elements.tip!==null){S.elements.tip.remove()
}R=S.options.style.tip.color||S.options.style.border.color;
if(S.options.style.tip.corner===false){return 
}else{if(!U){U=S.options.style.tip.corner
}}W=B(U,S.options.style.tip.size.width,S.options.style.tip.size.height);
S.elements.tip='<div class="'+S.options.style.classes.tip+'" dir="ltr" rel="'+U+'" style="position:absolute; height:'+S.options.style.tip.size.height+"px; width:"+S.options.style.tip.size.width+'px; margin:0 auto; line-height:0.1px; font-size:1px;">';
if(F("<canvas>").get(0).getContext){S.elements.tip+='<canvas height="'+S.options.style.tip.size.height+'" width="'+S.options.style.tip.size.width+'"></canvas>'
}else{if(F.browser.msie){T=S.options.style.tip.size.width+","+S.options.style.tip.size.height;
V="m"+W[0][0]+","+W[0][1];
V+=" l"+W[1][0]+","+W[1][1];
V+=" "+W[2][0]+","+W[2][1];
V+=" xe";
S.elements.tip+='<v:shape fillcolor="'+R+'" stroked="false" filled="true" path="'+V+'" coordsize="'+T+'" style="width:'+S.options.style.tip.size.width+"px; height:"+S.options.style.tip.size.height+"px; line-height:0.1px; display:inline-block; behavior:url(#default#VML); vertical-align:"+((U.search(/top/)!==-1)?"bottom":"top")+'"></v:shape>';
S.elements.tip+='<v:image style="behavior:url(#default#VML);"></v:image>';
S.elements.contentWrapper.css("position","relative")
}}S.elements.tooltip.prepend(S.elements.tip+"</div>");
S.elements.tip=S.elements.tooltip.find("."+S.options.style.classes.tip).eq(0);
if(F("<canvas>").get(0).getContext){H.call(S,S.elements.tip.find("canvas:first"),W,R)
}if(U.search(/top/)!==-1&&F.browser.msie&&parseInt(F.browser.version.charAt(0))===6){S.elements.tip.css({marginTop:-4})
}M.call(S,U)
}function H(S,U,R){var T=S.get(0).getContext("2d");
T.fillStyle=R;
T.beginPath();
T.moveTo(U[0][0],U[0][1]);
T.lineTo(U[1][0],U[1][1]);
T.lineTo(U[2][0],U[2][1]);
T.fill()
}function M(T){var S,V,R,W,U;
S=this;
if(S.options.style.tip.corner===false||!S.elements.tip){return 
}if(!T){T=S.elements.tip.attr("rel")
}V=positionAdjust=(F.browser.msie)?1:0;
S.elements.tip.css(T.match(/left|right|top|bottom/)[0],0);
if(T.search(/top|bottom/)!==-1){if(F.browser.msie){if(parseInt(F.browser.version.charAt(0))===6){positionAdjust=(T.search(/top/)!==-1)?-3:1
}else{positionAdjust=(T.search(/top/)!==-1)?1:2
}}if(T.search(/Middle/)!==-1){S.elements.tip.css({left:"50%",marginLeft:-(S.options.style.tip.size.width/2)})
}else{if(T.search(/Left/)!==-1){S.elements.tip.css({left:S.options.style.border.radius-V})
}else{if(T.search(/Right/)!==-1){S.elements.tip.css({right:S.options.style.border.radius+V})
}}}if(T.search(/top/)!==-1){S.elements.tip.css({top:-positionAdjust})
}else{S.elements.tip.css({bottom:positionAdjust})
}}else{if(T.search(/left|right/)!==-1){if(F.browser.msie){positionAdjust=(parseInt(F.browser.version.charAt(0))===6)?1:((T.search(/left/)!==-1)?1:2)
}if(T.search(/Middle/)!==-1){S.elements.tip.css({top:"50%",marginTop:-(S.options.style.tip.size.height/2)})
}else{if(T.search(/Top/)!==-1){S.elements.tip.css({top:S.options.style.border.radius-V})
}else{if(T.search(/Bottom/)!==-1){S.elements.tip.css({bottom:S.options.style.border.radius+V})
}}}if(T.search(/left/)!==-1){S.elements.tip.css({left:-positionAdjust})
}else{S.elements.tip.css({right:positionAdjust})
}}}R="padding-"+T.match(/left|right|top|bottom/)[0];
W=S.options.style.tip.size[(R.search(/left|right/)!==-1)?"width":"height"];
S.elements.tooltip.css("padding",0);
S.elements.tooltip.css(R,W);
if(F.browser.msie&&parseInt(F.browser.version.charAt(0))==6){U=parseInt(S.elements.tip.css("margin-top"))||0;
U+=parseInt(S.elements.content.css("margin-top"))||0;
S.elements.tip.css({marginTop:U})
}}function I(){var R=this;
if(R.elements.title!==null){R.elements.title.remove()
}R.elements.title=F('<div class="'+R.options.style.classes.title+'">').css(P(R.options.style.title,true)).css({zoom:(F.browser.msie)?1:0}).prependTo(R.elements.contentWrapper);
if(R.options.content.title.text){R.updateTitle.call(R,R.options.content.title.text)
}if(R.options.content.title.button!==false&&typeof R.options.content.title.button=="string"){R.elements.button=F('<a class="'+R.options.style.classes.button+'" style="float:right; position: relative"></a>').css(P(R.options.style.button,true)).html(R.options.content.title.button).prependTo(R.elements.title).click(function(S){if(!R.status.disabled){R.hide(S)
}})
}}function K(){var S,U,T,R;
S=this;
U=S.options.show.when.target;
T=S.options.hide.when.target;
if(S.options.hide.fixed){T=T.add(S.elements.tooltip)
}if(S.options.hide.when.event=="inactive"){R=["click","dblclick","mousedown","mouseup","mousemove","mouseout","mouseenter","mouseleave","mouseover"];
function X(Y){if(S.status.disabled===true){return 
}clearTimeout(S.timers.inactive);
S.timers.inactive=setTimeout(function(){F(R).each(function(){T.unbind(this+".qtip-inactive");
S.elements.content.unbind(this+".qtip-inactive")
});
S.hide(Y)
},S.options.hide.delay)
}}else{if(S.options.hide.fixed===true){S.elements.tooltip.bind("mouseover.qtip",function(){if(S.status.disabled===true){return 
}clearTimeout(S.timers.hide)
})
}}function W(Y){if(S.status.disabled===true){return 
}if(S.options.hide.when.event=="inactive"){F(R).each(function(){T.bind(this+".qtip-inactive",X);
S.elements.content.bind(this+".qtip-inactive",X)
});
X()
}clearTimeout(S.timers.show);
clearTimeout(S.timers.hide);
S.timers.show=setTimeout(function(){S.show(Y)
},S.options.show.delay)
}function V(Y){if(S.status.disabled===true){return 
}if(S.options.hide.fixed===true&&S.options.hide.when.event.search(/mouse(out|leave)/i)!==-1&&F(Y.relatedTarget).parents("div.qtip[qtip]").length>0){Y.stopPropagation();
Y.preventDefault();
clearTimeout(S.timers.hide);
return false
}clearTimeout(S.timers.show);
clearTimeout(S.timers.hide);
S.elements.tooltip.stop(true,true);
S.timers.hide=setTimeout(function(){S.hide(Y)
},S.options.hide.delay)
}if((S.options.show.when.target.add(S.options.hide.when.target).length===1&&S.options.show.when.event==S.options.hide.when.event&&S.options.hide.when.event!=="inactive")||S.options.hide.when.event=="unfocus"){S.cache.toggle=0;
U.bind(S.options.show.when.event+".qtip",function(Y){if(S.cache.toggle==0){W(Y)
}else{V(Y)
}})
}else{U.bind(S.options.show.when.event+".qtip",W);
if(S.options.hide.when.event!=="inactive"){T.bind(S.options.hide.when.event+".qtip",V)
}}if(S.options.position.type.search(/(fixed|absolute)/)!==-1){S.elements.tooltip.bind("mouseover.qtip",S.focus)
}if(S.options.position.target==="mouse"&&S.options.position.type!=="static"){U.bind("mousemove.qtip",function(Y){S.cache.mouse={x:Y.pageX,y:Y.pageY};
if(S.status.disabled===false&&S.options.position.adjust.mouse===true&&S.options.position.type!=="static"&&S.elements.tooltip.css("display")!=="none"){S.updatePosition(Y)
}})
}}function N(T,U,Z){var Y,R,W,X,S,V;
Y=this;
if(Z.corner=="center"){return U.position
}R=F.extend({},T);
X={x:false,y:false};
S={left:(R.left<F.fn.qtip.cache.screen.scroll.left),right:(R.left+Z.dimensions.width+2>=F.fn.qtip.cache.screen.width+F.fn.qtip.cache.screen.scroll.left),top:(R.top<F.fn.qtip.cache.screen.scroll.top),bottom:(R.top+Z.dimensions.height+2>=F.fn.qtip.cache.screen.height+F.fn.qtip.cache.screen.scroll.top)};
W={left:(S.left&&(Z.corner.search(/right/i)!=-1||(Z.corner.search(/right/i)==-1&&!S.right))),right:(S.right&&(Z.corner.search(/left/i)!=-1||(Z.corner.search(/left/i)==-1&&!S.left))),top:(S.top&&Z.corner.search(/top/i)==-1),bottom:(S.bottom&&Z.corner.search(/bottom/i)==-1)};
if(W.left){if(Y.options.position.target!=="mouse"){R.left=U.position.left+U.dimensions.width
}else{R.left=Y.cache.mouse.x
}X.x="Left"
}else{if(W.right){if(Y.options.position.target!=="mouse"){R.left=U.position.left-Z.dimensions.width
}else{R.left=Y.cache.mouse.x-Z.dimensions.width
}X.x="Right"
}}if(W.top){if(Y.options.position.target!=="mouse"){R.top=U.position.top+U.dimensions.height
}else{R.top=Y.cache.mouse.y
}X.y="top"
}else{if(W.bottom){if(Y.options.position.target!=="mouse"){R.top=U.position.top-Z.dimensions.height
}else{R.top=Y.cache.mouse.y-Z.dimensions.height
}X.y="bottom"
}}if(R.left<0){R.left=T.left;
X.x=false
}if(R.top<0){R.top=T.top;
X.y=false
}if(Y.options.style.tip.corner!==false){R.corner=new String(Z.corner);
if(X.x!==false){R.corner=R.corner.replace(/Left|Right|Middle/,X.x)
}if(X.y!==false){R.corner=R.corner.replace(/top|bottom/,X.y)
}if(R.corner!==Y.elements.tip.attr("rel")){E.call(Y,R.corner)
}}return R
}function P(T,S){var U,R;
U=F.extend(true,{},T);
for(R in U){if(S===true&&R.search(/(tip|classes)/i)!==-1){delete U[R]
}else{if(!S&&R.search(/(width|border|tip|title|classes|user)/i)!==-1){delete U[R]
}}}return U
}function C(R){if(typeof R.tip!=="object"){R.tip={corner:R.tip}
}if(typeof R.tip.size!=="object"){R.tip.size={width:R.tip.size,height:R.tip.size}
}if(typeof R.border!=="object"){R.border={width:R.border}
}if(typeof R.width!=="object"){R.width={value:R.width}
}if(typeof R.width.max=="string"){R.width.max=parseInt(R.width.max.replace(/([0-9]+)/i,"$1"))
}if(typeof R.width.min=="string"){R.width.min=parseInt(R.width.min.replace(/([0-9]+)/i,"$1"))
}if(typeof R.tip.size.x=="number"){R.tip.size.width=R.tip.size.x;
delete R.tip.size.x
}if(typeof R.tip.size.y=="number"){R.tip.size.height=R.tip.size.y;
delete R.tip.size.y
}return R
}function A(){var R,S,T,W,U,V;
R=this;
T=[true,{}];
for(S=0;
S<arguments.length;
S++){T.push(arguments[S])
}W=[F.extend.apply(F,T)];
while(typeof W[0].name=="string"){W.unshift(C(F.fn.qtip.styles[W[0].name]))
}W.unshift(true,{classes:{tooltip:"qtip-"+(arguments[0].name||"defaults")}},F.fn.qtip.styles.defaults);
U=F.extend.apply(F,W);
V=(F.browser.msie)?1:0;
U.tip.size.width+=V;
U.tip.size.height+=V;
if(U.tip.size.width%2>0){U.tip.size.width+=1
}if(U.tip.size.height%2>0){U.tip.size.height+=1
}if(U.tip.corner===true){U.tip.corner=(R.options.position.corner.tooltip==="center")?false:R.options.position.corner.tooltip
}return U
}function B(U,T,S){var R={bottomRight:[[0,0],[T,S],[T,0]],bottomLeft:[[0,0],[T,0],[0,S]],topRight:[[0,S],[T,0],[T,S]],topLeft:[[0,0],[0,S],[T,S]],topMiddle:[[0,S],[T/2,0],[T,S]],bottomMiddle:[[0,0],[T,0],[T/2,S]],rightMiddle:[[0,0],[T,S/2],[0,S]],leftMiddle:[[T,0],[T,S],[0,S/2]]};
R.leftTop=R.bottomRight;
R.rightTop=R.bottomLeft;
R.leftBottom=R.topRight;
R.rightBottom=R.topLeft;
return R[U]
}function G(R){var S;
if(F("<canvas>").get(0).getContext){S={topLeft:[R,R],topRight:[0,R],bottomLeft:[R,0],bottomRight:[0,0]}
}else{if(F.browser.msie){S={topLeft:[-90,90,0],topRight:[-90,90,-R],bottomLeft:[90,270,0],bottomRight:[90,270,-R]}
}}return S
}function J(){var R,S,T;
R=this;
T=R.getDimensions();
S='<iframe class="qtip-bgiframe" frameborder="0" tabindex="-1" src="javascript:false" style="display:block; position:absolute; z-index:-1; filter:alpha(opacity=\'0\'); border: 1px solid red; height:'+T.height+"px; width:"+T.width+'px" />';
R.elements.bgiframe=R.elements.wrapper.prepend(S).children(".qtip-bgiframe:first")
}F(document).ready(function(){F.fn.qtip.cache={screen:{scroll:{left:F(window).scrollLeft(),top:F(window).scrollTop()},width:F(window).width(),height:F(window).height()}};
var R;
F(window).bind("resize scroll",function(S){clearTimeout(R);
R=setTimeout(function(){if(S.type==="scroll"){F.fn.qtip.cache.screen.scroll={left:F(window).scrollLeft(),top:F(window).scrollTop()}
}else{F.fn.qtip.cache.screen.width=F(window).width();
F.fn.qtip.cache.screen.height=F(window).height()
}for(i=0;
i<F.fn.qtip.interfaces.length;
i++){var T=F.fn.qtip.interfaces[i];
if(T.status.rendered===true&&(T.options.position.type!=="static"||T.options.position.adjust.scroll&&S.type==="scroll"||T.options.position.adjust.resize&&S.type==="resize")){T.updatePosition(S,true)
}}},100)
});
F(document).bind("mousedown.qtip",function(S){if(F(S.target).parents("div.qtip").length===0){F(".qtip[unfocus]").each(function(){var T=F(this).qtip("api");
if(F(this).is(":visible")&&!T.status.disabled&&F(S.target).add(T.elements.target).length>1){T.hide(S)
}})
}})
});
F.fn.qtip.interfaces=[];
F.fn.qtip.log={error:function(){return this
}};
F.fn.qtip.constants={};
F.fn.qtip.defaults={content:{prerender:false,text:false,url:false,data:null,title:{text:false,button:false}},position:{target:false,corner:{target:"bottomRight",tooltip:"topLeft"},adjust:{x:0,y:0,mouse:true,screen:false,scroll:true,resize:true},type:"absolute",container:false},show:{when:{target:false,event:"mouseover"},effect:{type:"fade",length:100},delay:140,solo:false,ready:false},hide:{when:{target:false,event:"mouseout"},effect:{type:"fade",length:100},delay:0,fixed:false},api:{beforeRender:function(){},onRender:function(){},beforePositionUpdate:function(){},onPositionUpdate:function(){},beforeShow:function(){},onShow:function(){},beforeHide:function(){},onHide:function(){},beforeContentUpdate:function(){},onContentUpdate:function(){},beforeContentLoad:function(){},onContentLoad:function(){},beforeTitleUpdate:function(){},onTitleUpdate:function(){},beforeDestroy:function(){},onDestroy:function(){},beforeFocus:function(){},onFocus:function(){}}};
F.fn.qtip.styles={defaults:{background:"white",color:"#111",overflow:"hidden",textAlign:"left",width:{min:0,max:250},padding:"5px 9px",border:{width:1,radius:0,color:"#d3d3d3"},tip:{corner:false,color:false,size:{width:13,height:13},opacity:1},title:{background:"#e1e1e1",fontWeight:"bold",padding:"7px 12px"},button:{cursor:"pointer"},classes:{target:"",tip:"qtip-tip",title:"qtip-title",button:"qtip-button",content:"qtip-content",active:"qtip-active"}},cream:{border:{width:3,radius:0,color:"#F9E98E"},title:{background:"#F0DE7D",color:"#A27D35"},background:"#FBF7AA",color:"#A27D35",classes:{tooltip:"qtip-cream"}},light:{border:{width:3,radius:0,color:"#E2E2E2"},title:{background:"#f1f1f1",color:"#454545"},background:"white",color:"#454545",classes:{tooltip:"qtip-light"}},dark:{border:{width:3,radius:0,color:"#303030"},title:{background:"#404040",color:"#f3f3f3"},background:"#505050",color:"#f3f3f3",classes:{tooltip:"qtip-dark"}},red:{border:{width:3,radius:0,color:"#CE6F6F"},title:{background:"#f28279",color:"#9C2F2F"},background:"#F79992",color:"#9C2F2F",classes:{tooltip:"qtip-red"}},green:{border:{width:3,radius:0,color:"#A9DB66"},title:{background:"#b9db8c",color:"#58792E"},background:"#CDE6AC",color:"#58792E",classes:{tooltip:"qtip-green"}},blue:{border:{width:3,radius:0,color:"#ADD9ED"},title:{background:"#D0E9F5",color:"#5E99BD"},background:"#E5F6FE",color:"#4D9FBF",classes:{tooltip:"qtip-blue"}}}
})(jQuery);
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (66043)");
        console.log(err);
    }
}

try {
// This file was automatically generated from attachments.soy.
// Please don't edit this file by hand.

if (typeof Confluence == 'undefined') { var Confluence = {}; }
if (typeof Confluence.Templates == 'undefined') { Confluence.Templates = {}; }
if (typeof Confluence.Templates.Attachments == 'undefined') { Confluence.Templates.Attachments = {}; }


Confluence.Templates.Attachments.removalConfirmationTitle = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append(soy.$$escapeHtml("Attachment Removal Confirmation"));
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Attachments.removalConfirmationBody = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<div>', soy.$$escapeHtml(AJS.format("Are you sure you want to remove the attached file {0}?",opt_data.filename)), '</div>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Attachments.versionRemovalConfirmationTitle = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append(soy.$$escapeHtml("Attachment Version Removal Confirmation"));
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Attachments.versionRemovalConfirmationBody = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<div>', soy.$$escapeHtml(AJS.format("Are you sure you want to remove version {0} of the attached file {1}?",opt_data.version,opt_data.filename)), '</div>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Attachments.removalErrorTitle = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append(soy.$$escapeHtml("Attachment Removal Error"));
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Attachments.removalErrorBody = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<div class="aui-message error">');
  if (! opt_data.messages) {
    output.append(soy.$$escapeHtml("An error occurred while trying to remove the attachment.  Please check the current state. The file may have been removed already."));
  } else {
    if (opt_data.messages.length == 1) {
      var messageList24 = opt_data.messages;
      var messageListLen24 = messageList24.length;
      for (var messageIndex24 = 0; messageIndex24 < messageListLen24; messageIndex24++) {
        var messageData24 = messageList24[messageIndex24];
        output.append(soy.$$escapeHtml(messageData24));
      }
    } else {
      output.append('<ul>');
      var messageList29 = opt_data.messages;
      var messageListLen29 = messageList29.length;
      for (var messageIndex29 = 0; messageIndex29 < messageListLen29; messageIndex29++) {
        var messageData29 = messageList29[messageIndex29];
        output.append('<li>', soy.$$escapeHtml(messageData29), '</li>');
      }
      output.append('</ul>');
    }
  }
  output.append('</div>');
  return opt_sb ? '' : output.toString();
};

} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (41758)");
        console.log(err);
    }
}

try {
AJS.Attachments={showOlderVersions:function(a){a(".attachment-history a").click(function(d){var b=a(this).parents("table.attachments");var c=a(this).parents("tr:first")[0].id.substr(11);var f=a(".history-"+c,b);a(this).toggleClass("icon-section-opened");a(this).toggleClass("icon-section-closed");f.toggleClass("hidden");return AJS.stopEvent(d)})}};AJS.toInit(function(d){var i=d("#more-attachments-link");i.click(function(k){d(".more-attachments").removeClass("hidden");i.addClass("hidden");return AJS.stopEvent(k)});AJS.Attachments.showOlderVersions(d);var g=AJS.Confluence.Templates.Attachments;function a(k,l){return d(k).parents("["+l+"]").attr(l)}function e(k){return a(k,"data-attachment-filename")}function b(k){return a(k,"data-attachment-version")}function c(m,n,k,l){n=n||{};AJS.safe.ajax({type:"POST",url:m,data:n,success:k,error:l})}function j(l){var k=AJS.ConfluenceDialog({width:550,height:200,id:l});return k}function h(k,l,n){var m=j("attachment-removal-confirm-dialog");m.addHeader(l);m.addPanel("",n);m.addSubmit("Remove",function(){var o=function(r,s,q){location.reload(true)};var p=function(s,v,u){var r=null;if(s.responseText){var q=d.parseJSON(s.responseText);if(q.actionErrors){r=q.actionErrors}}var t=j("attachment-removal-error-dialog");t.addHeader(g.removalErrorTitle());t.addPanel("",g.removalErrorBody({messages:r}));t.addButton("Close",function(){location.reload(true)});t.show();m.remove()};c(k,null,o,p)});m.addCancel("Cancel",function(){m.remove()});m.show()}function f(l,k){return AJS.Confluence.getContextPath()+l+k}d(".removeAttachmentLink").click(function(k){h(f("/json/removeattachment.action",this.search),g.removalConfirmationTitle(),g.removalConfirmationBody({filename:e(this)}));return false});d(".removeAttachmentLinkVersion").click(function(k){h(f("/json/removeattachmentversion.action",this.search),g.versionRemovalConfirmationTitle(),g.versionRemovalConfirmationBody({filename:e(this),version:b(this)}));return false})});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (65554)");
        console.log(err);
    }
}

try {
AJS.$(function(C){var B=AJS.params.contextPath;
var D="Error retrieving old versions of attachment.";
var A={getFieldSetAsParams:function(E){var F={};
E.find("input").each(function(){F[this.name]=this.value
});
return F
},initAttachmentTable:function(E){C(".attachment-menu-bar .ajs-menu-item").unbind();
C(".attachment-menu-bar").ajsMenu();
C(".attachments .attachment-row .attachment-dropdown-actions .action-dropdown",E).addClass("hide-menu");
C(".attachments .attachment-row .attachment-dropdown-actions .action-dropdown .aui-dd-parent .aui-dropdown",E).addClass("hidden");
C(".attachments .attachment-dropdown-actions span",E).addClass("hide-icons");
var F=this.getFieldSetAsParams(E.children("fieldset"));
C(".attachments .attachment-row",E).each(function(){var G=C(this);
C(G).hover(function(){if(!G.hasClass("focused")){G.addClass("hovered")
}C("span",G).removeClass("hide-icons");
C("span",G).removeClass("hide-menu");
C(".attachment-dropdown-actions .action-dropdown",G).dropDown("Standard",{alignment:"right"})
},function(){G.removeClass("hovered");
var H=C(".attachment-dropdown-actions",G);
if(!H.hasClass("active")){C("span",G).addClass("hide-icons")
}else{G.addClass("focused")
}if(G.hasClass("focused")){C("span",G).removeClass("hide-icons")
}})
});
C(".attachments .aui-dd-trigger",E).click(function(){var G=C(this).parents("tr:first")[0].id;
C(".attachments .attachment-row",E).each(function(){var I=C(this);
var J=I[0].id;
var H=(".attachment-dropdown-actions",I);
if(J==G){I.addClass("focused");
C("span",I).removeClass("hide-icons")
}else{if(I.hasClass("focused")){I.removeClass("focused");
C("span",I).addClass("hide-icons")
}}C(".attachment-dropdown-actions .action-dropdown",I).dropDown("Standard",{alignment:"right"})
})
});
C(".attachments .attachment-row .filename-column a.filename",E).each(function(){A.attachmentFancyBox(C(this))
})
},markFileCommentFieldModified:function(E){if(E.hasClass("blank-search")){E.removeClass("blank-search");
E.val("")
}},initAttachmentCommentTextFields:function(E){E.find("input[name^='comment_']").each(function(){C(this).focus(function(){A.markFileCommentFieldModified(C(this))
})
})
},renameForms:function(){var E=self.placeFocus;
if(E){self.placeFocus=function(){var F=C("div.plugin_attachments_upload_container form");
F.attr("name","inlinecommentform");
E();
F.removeAttr("name")
}
}},refreshOtherAttachmentsMacroInstances:function(G,E,F){C("div.plugin_attachments_table_container > fieldset").each(function(){var J=C(this);
var H=C("input[name='pageId']",J).val();
if(H==G){var I=C(this).clone();
C("input",I).each(function(){if(!C(this).hasClass("plugin_attachments_macro_render_param")){C(this).remove()
}});
C.ajax({cache:false,type:"GET",url:B+"/pages/plugins/attachments/rendermacro.action",data:A.getFieldSetAsParams(I),success:function(M){var K=J.parent();
var L=C(M).find("div.plugin_attachments_table_container").html();
K.fadeOut("normal",function(){K.html(L);
A.initAttachmentTable(K)
});
K.fadeIn("normal",function(){AJS.Labels.bindOpenDialog(AJS.$(".show-labels-editor",K))
});
if(E){E()
}},error:function(){if(F){F()
}}})
}})
},initUploadForm:function(I){this.initAttachmentCommentTextFields(I);
var H=I.children("iframe.plugin_attachments_uploadiframe");
var G=I.find("input[name='confirm']");
I.find("input[name='file_0']").val("");
G.after("<img src='"+B+"/images/icons/wait.gif' class='plugin_attachments_uploadwaiticon hidden'/>");
var E=G.next("img.plugin_attachments_uploadwaiticon");
var F=A.getFieldSetAsParams(I.parent().prev("div.plugin_attachments_table_container").children("fieldset"));
I.submit(function(){if(F.outputType=="preview"){return false
}I.find("input[name^='comment_']").each(function(){A.markFileCommentFieldModified(C(this))
});
var J=this;
J.target=H.attr("name");
G.addClass("hidden");
E.removeClass("hidden");
H.get(0).processUpload=true;
return true
});
H.load(function(){if(!this.processUpload){return 
}var P=this.contentWindow||this.contentDocument;
P=P.document?P.document:P;
var N=P.body;
var M=C(N);
var L=M.find("div.errorBox");
var K=I.parent();
var O=K.children("div.errorBox");
var J=K.children("div.successBox");
if(L.length>0&&C.trim(L.html()).length>0){O.html(L.html());
O.removeClass("hidden");
J.addClass("hidden");
E.addClass("hidden");
G.removeClass("hidden")
}else{A.refreshOtherAttachmentsMacroInstances(F.pageId,function(){O.addClass("hidden");
J.removeClass("hidden");
E.addClass("hidden");
G.removeClass("hidden")
},function(){O.html(F["i18n-notpermitted"]);
O.removeClass("hidden");
J.addClass("hidden");
E.addClass("hidden");
G.removeClass("hidden")
});
I.find("input[name='file_0']").replaceWith(C('<input type="file" name="file_0" size="30">'))
}})
},hideDropdownIcons:function(){C(".attachments .attachment-row").each(function(){var E=C(this);
if(E.hasClass("focused")){E.removeClass("focused");
C("span",E).addClass("hide-icons")
}})
},loadOldVersions:function(E){C(".attachment-row .attachment-history a",E).click(function(){var G=C(this);
if(G.hasClass("icon-section-opened")){var I=G.parents("tr:first");
var J=I[0].id.substr(("attachment-").length);
if(C("tr.history-"+J,E).length==0){var H=A.getFieldSetAsParams(E.children(".hiddenfieldset"));
H.attachmentId=J;
H.decorator="none";
var F=C(".waiting-image",I).removeClass("hidden");
C.ajax({cache:false,data:H,dataType:"html",success:function(K){var L=C(document.createElement("div")).html(K);
C("a.filename",L).each(function(){A.attachmentFancyBox(C(this))
});
I.after(C("tr",L));
F.addClass("hidden")
},error:function(K,N,L){AJS.log("Error retrieving attachment old versions: "+L);
var M=E.parent(".plugin_attachments_container");
C(E).remove();
C("div.plugin_attachments_upload_container",M).remove();
C(M).append('<div class="attachments-old-version-error error">'+D+"</div>");
F.addClass("hidden")
},type:"GET",url:B+"/pages/plugins/attachments/loadoldversions.action"});
return false
}}})
},attachmentFancyBox:function(F){var E=F.attr("data-type").toLowerCase();
if(E.indexOf("image")!=-1){F.fancybox()
}}};
C("body").click(function(F){var E=C(F.target);
if(E.parents("td").length&&!E.parents("td").hasClass("attachment-dropdown-actions")){A.hideDropdownIcons()
}});
C("div.plugin_attachments_table_container").each(function(){var E=C(this);
A.initAttachmentTable(E);
A.loadOldVersions(E)
});
C("form.plugin_attachments_uploadform").each(function(){A.initUploadForm(C(this))
});
A.renameForms()
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (34164)");
        console.log(err);
    }
}

try {
jQuery.fn.bindUp=function(B,A){B=B.split(/\s+/);
this.each(function(){var C=B.length;
while(C--){jQuery(this).bind(B[C],A);
var D=jQuery.data(this,"events")[B[C]];
D.splice(0,0,D.pop())
}})
};
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (59191)");
        console.log(err);
    }
}

try {
Lockpoint.OC={docUrl:null,allUrls:Array(),jumpThroughHoops:function(A,B){for(var C=0;
C<A.length;
C++){Lockpoint.OC.docUrl=null;
jQuery(A[C]).mouseover();
if(Lockpoint.OC.docUrl){B(A[C],Lockpoint.OC.docUrl)
}}},processXlsAndDocAjaxResponse:function(B){if(!B.attachments){return 
}for(var A=0;
A<B.attachments.length;
A++){var C=B.attachments[A];
if(C.checkedOutToAnyone==0||C.checkedOutToCurrentUser!=0){continue
}Lockpoint.OC.jumpThroughHoops(jQuery("input.checkout-office"),function(G,F){if(F==C.url){var E=jQuery(G).parent().parent();
var D=E.find(".confluence-button");
if(D.size()==0){E=E.parent();
D=E.find(".confluence-button")
}D.addClass("hidden");
D.after('<div class="arsenale-warning"><div class="lpfilename"><div class="lockpointicon lockedbysomeoneelse">&nbsp;</div><span class="lockedFilename">'+C.filenameHtml+'</span></div><p class="lpinfo"><span class="lockedByLabel">'+"Locked by:"+'</span> <span class="lockedBy">'+C.ownerDisplayStringHtml+'</span><br/><span class="lockedOnLabel">'+"Locked on:"+'</span> <span class="lockedOn">'+C.checkedOutDate+"</span></p></div>")
}})
}},processXlsAndDoc:function(){var A=jQuery("input.checkout-office");
Lockpoint.OC.jumpThroughHoops(A,function(D,C){Lockpoint.OC.allUrls.push(C)
});
if(Lockpoint.OC.allUrls.length==0){return 
}var B={url:Lockpoint.contextPath+"/lockpoint/ajaxhelper.action",type:"POST",dataType:"json",data:({urls:Lockpoint.OC.allUrls}),success:Lockpoint.OC.processXlsAndDocAjaxResponse};
jQuery.ajax(B)
},processPpt:function(){var B=jQuery(".vf-slide-viewer-macro");
var A=[];
B.each(function(C,D){A.push({ceoId:jQuery(D).attr("data-page-id"),filename:jQuery(D).attr("data-attachment")})
});
if(A.length==0){return 
}jQuery.ajax({url:Lockpoint.contextPath+"/rest/lockpoint/1.0/attachment-status",dataType:"json",contentType:"application/json; charset=UTF-8",data:JSON.stringify(A),type:"POST",success:function(C){for(var D=0;
D<C.length;
D++){var E=C[D];
if(!E.accessibleToCurrentUser){B.each(function(F,H){var J=jQuery(H).attr("data-attachment");
if(E.requestedFilename==J){var G=jQuery(H).find("button.edit");
G.attr("disabled",true);
var I=jQuery(H).find(".vf-slide-viewer");
I.append('<div class="lppowerpointwarning">'+E.lockWarningMessageHtml+"</div>")
}})
}}}})
}};
jQuery(function(A){Lockpoint.OC.processXlsAndDoc();
Lockpoint.OC.processPpt()
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (9434)");
        console.log(err);
    }
}

try {
Lockpoint.Mockups={baseMockupSelector:"a.balsamiq_mockup_link",processOneMockup:function(B,A){if(B.checkedOutToAnyone==0){jQuery(A).hide();
jQuery(A).after('<a href="'+B.checkoutUrl+'">'+"Lock to Edit"+"</a>")
}else{if(B.checkedOutToCurrentUser==1){jQuery(A).attr("href",B.safeEditUrl);
jQuery(A).after(' &nbsp; <a href="'+B.checkinUrl+'">'+"Unlock"+"</a>")
}else{jQuery(A).hide();
jQuery(A).after('<div class="arsenale-warning-nof"><div class="lockpointicon lockedbysomeoneelse">&nbsp;</div><strong>'+B.filenameHtml+"</strong> "+AJS.format("locked by {0} on {1}",B.ownerDisplayStringHtml,B.checkedOutDate)+"</div>")
}}},processAjaxResponse:function(E){if(!E.attachments){return 
}for(var D=0;
D<E.attachments.length;
D++){var F=E.attachments[D];
var A=Lockpoint.Mockups.baseMockupSelector+"[href='"+F.url+"']";
var C=jQuery(A);
for(var B=0;
B<C.length;
B++){Lockpoint.Mockups.processOneMockup(F,C[B])
}}},init:function(){var A=jQuery(Lockpoint.Mockups.baseMockupSelector);
var C=new Array();
for(var B=0;
B<A.length;
B++){var E=jQuery(A[B]).attr("href");
C.push(E)
}if(C.length==0){return 
}var D={url:Lockpoint.contextPath+"/lockpoint/ajaxhelper.action",type:"POST",dataType:"json",data:({mockupUrls:C}),success:Lockpoint.Mockups.processAjaxResponse};
jQuery.ajax(D)
}};
jQuery(function(A){Lockpoint.Mockups.init()
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (18722)");
        console.log(err);
    }
}

try {
Lockpoint.PageAndBlog={getFieldSetAsParams:function(A){var B={};
A.find("input").each(function(){B[this.name]=this.value
});
return B
},handleAttachmentsMacroRemoval:function(){jQuery("a.removeAttachmentLinkArsenale").each(function(){var A=jQuery(this).closest("div.plugin_attachments_table_container");
var D=Lockpoint.PageAndBlog.getFieldSetAsParams(A.children("fieldset"));
var B=jQuery(this);
var C=jQuery(B.parents("tr")[0]);
B.unbind();
B.click(function(){if(!jQuery(this).hasClass("lockpointDisabled")){var E=jQuery(".filename",C).attr("data-filename");
if(confirm(AJS.format(D.deleteConfirmMessage,E))){window.location.href=B.attr("href")
}}return false
})
})
},handleAttachmentsMacroUploadForm:function(){var A=jQuery("div.plugin_attachments_upload_container form");
if(!A){return 
}A.unbind();
A.bind("submit",function(B){var C=jQuery("div.plugin_attachments_upload_container form input.blank-search");
if(C){C.attr("value","")
}})
},setupAttachmentsMacroFileHandlersAndSetInitialState:function(){var A=jQuery("tr.attachment-summary");
A.each(function(){var B=jQuery(this);
Lockpoint.PageAndBlog.attachAttachmentLinkHandlers(B);
Lockpoint.PageAndBlog.setAttachmentLinkStatus(B,B.hasClass("locked"),B.hasClass("lockedbyus"))
})
},stopClickHandler:function(A){A.stopImmediatePropagation();
A.preventDefault();
return false
},setAttachmentLinkStatus:function(H,E,D){var J=H.find("a.lockAttachmentLink");
var G=H.find("a.unlockAttachmentLink");
var F=H.find("a.forceUnlockAttachmentLink");
var B=H.find("a.editAttachmentLink");
var I=H.find("a.removeAttachmentLink");
var C=H.find("a.editInGliffyHtml5,a.editInGliffyNewWindow,a.editInGliffySameWindow");
var A=H.find("a.office-editable,a.office-editable-pathauth");
enableButton=function(L,K){if(K){L.removeClass("lockpointDisabled");
L.unbind("click",Lockpoint.PageAndBlog.stopClickHandler)
}else{L.addClass("lockpointDisabled");
L.bindUp("click",Lockpoint.PageAndBlog.stopClickHandler)
}};
enableButton(J,!E);
enableButton(G,D);
enableButton(F,E&&!D);
enableButton(B,D);
enableButton(I,!E||D);
enableButton(C,D);
enableButton(A,!E||D)
},setCurrentOwner:function(C,B){var A=C.prev().find("td.lockstatus");
A.html(B)
},lockSuccessCallback:function(A,B){B.parentTr.removeClass("unlocked").addClass("locked").addClass("lockedbyus");
Lockpoint.PageAndBlog.setAttachmentLinkStatus(B.parentTr,true,true);
Lockpoint.PageAndBlog.setCurrentOwner(B.parentTr,A.renderedLockStatusHtml)
},unlockSuccessCallback:function(A,B){B.parentTr.removeClass("locked").removeClass("lockedbyus").addClass("unlocked");
Lockpoint.PageAndBlog.setAttachmentLinkStatus(B.parentTr,false,false);
Lockpoint.PageAndBlog.setCurrentOwner(B.parentTr,A.renderedLockStatusHtml)
},lockOrUnlockFailureCallback:function(A,D){var C;
if(A&&A.errorMessageHtml){C=A.errorMessageHtml
}else{C="Confluence reported an internal server error performing the action."
}var B=new AJS.Dialog({width:450,height:250,id:"lockpointStatusDialog"});
var E="Lockpoint Error";
B.addHeader(E,"lockpointDialogTitle");
B.addPanel(E,"mainPanel","lockpointStatusMessage",1);
jQuery("#lockpointStatusDialog .lockpointStatusMessage").html(C);
B.addButtonPanel();
B.addSubmit("OK",function(){B.remove();
return false
});
B.show()
},lockOrUnlockLinkClicked:function(B,F,D){if(!jQuery(B).hasClass("lockpointDisabled")){var C=jQuery(B).parents("tr.attachment-summary");
var A=AJS.params.pageId;
var G=C.attr("data-attachment-filename");
var E={ceoId:A,filename:G,lockMode:"confluence",failure:Lockpoint.PageAndBlog.lockOrUnlockFailureCallback,opts:{parentTr:C}};
if(D=="lock"){E.success=Lockpoint.PageAndBlog.lockSuccessCallback;
Lockpoint.performRestLock(E)
}else{E.success=Lockpoint.PageAndBlog.unlockSuccessCallback;
Lockpoint.performRestUnlock(E)
}}F.stopImmediatePropagation();
F.preventDefault();
return false
},lockLinkClicked:function(A){return Lockpoint.PageAndBlog.lockOrUnlockLinkClicked(this,A,"lock")
},unlockLinkClicked:function(A){return Lockpoint.PageAndBlog.lockOrUnlockLinkClicked(this,A,"unlock")
},attachAttachmentLinkHandlers:function(A){var D=A.find("a.lockAttachmentLink");
var C=A.find("a.unlockAttachmentLink");
var B=A.find("a.forceUnlockAttachmentLink");
D.click(Lockpoint.PageAndBlog.lockLinkClicked);
C.click(Lockpoint.PageAndBlog.unlockLinkClicked);
B.click(Lockpoint.PageAndBlog.unlockLinkClicked)
},handleGliffyDiagrams:function(){jQuery(".arsenale-gliffy-lock-button a").click(function(A){var G=jQuery(this).parents(".gliffy-container");
var B=G.attr("data-ceoid");
var H=G.attr("data-filename");
var F=G.find(".arsenale-gliffy-edit-button");
var C=G.find(".arsenale-gliffy-lock-button");
var E=G.find(".arsenale-gliffy-unlock-button");
var D=Lockpoint.performAjaxFileAction("lock",B,H);
if(D==""){F.show();
C.hide();
E.show()
}else{alert(D)
}A.stopPropagation();
A.preventDefault()
});
jQuery(".arsenale-gliffy-unlock-button a").click(function(A){var G=jQuery(this).parents(".gliffy-container");
var B=G.attr("data-ceoid");
var H=G.attr("data-filename");
var F=G.find(".arsenale-gliffy-edit-button");
var C=G.find(".arsenale-gliffy-lock-button");
var E=G.find(".arsenale-gliffy-unlock-button");
var D=Lockpoint.performAjaxFileAction("unlock",B,H);
if(D==""){F.hide();
C.show();
E.hide()
}else{alert(D)
}A.stopPropagation();
A.preventDefault()
})
}};
jQuery(function(A){Lockpoint.PageAndBlog.handleAttachmentsMacroUploadForm();
Lockpoint.PageAndBlog.handleAttachmentsMacroRemoval();
Lockpoint.PageAndBlog.setupAttachmentsMacroFileHandlersAndSetInitialState();
Lockpoint.PageAndBlog.handleGliffyDiagrams()
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (56817)");
        console.log(err);
    }
}

try {
AJS.toInit(function ($) {
    $("#action-view-source-link").click(function (e) {
        window.open(this.href, (this.id + "-popupwindow").replace(/-/g, "_"), "width=800, height=600, scrollbars, resizable");
            e.preventDefault();
            return false;
        });
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (7781)");
        console.log(err);
    }
}

try {
AJS.toInit(function ($) {
    $(".view-storage-link, .view-storage-link a").click(function (e) {
        window.open(this.href, (this.id + "-popupwindow").replace(/-/g, "_"), "width=800, height=600, scrollbars, resizable");
            e.preventDefault();
            return false;
        });
});

} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (32144)");
        console.log(err);
    }
}

try {
AJS.toInit(function($) {
    function autoSize(embeds, attempt) {
        var retry;

        if(attempt >= 20) { // 2 sec
            AJS.log('unable to auto size flash - took too long to load');
            return;
        }

        retry = $([]);

        embeds.each(function() {
            var $e = $(this);
            var h, w;
            if(this.GetVariable) {
                // Only set width/height is not already set
                if(!$e.attr("height")) {
                    h = this.GetVariable("height");
                    if(h) {
                        $e.height(h);
                    } else {
                        retry = retry.add($e);
                        return;
                    }
                }
                if(!$e.attr("width")) {
                    w = this.GetVariable("width");
                    if(w) {
                        $e.width(w);
                    } else {
                        retry = retry.add($e);
                        return;
                    }
                }
            }
        });

        if(retry.length) {
            setTimeout(function() {
                autoSize(retry, attempt + 1);
            }, 100);
        }
    }

    autoSize($('embed[type="application/x-shockwave-flash"]'), 0);

    // For preview
    $(window).bind("render-content-loaded", function(e, body) {
        autoSize($('embed[type="application/x-shockwave-flash"]', body), 0);
    });
});

} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (64100)");
        console.log(err);
    }
}

