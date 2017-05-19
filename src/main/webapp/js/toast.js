//弹出一个提示框,显示的html为msgstr,并且在time毫秒后自动删除提示框.time默认1500毫秒
/*调用举例:toast('错误')
toast('<img src=""  style=""><p>错误</p>')
toast('<p>错误</p>',3000)
*/
function toast(msgstr,time){
	var t=time?time:1500;
	var d=document.createElement('div');
	d.id='msg'+Date.now();
	d.style.cssText='position: fixed; top: 25%; left: 50%;width: 30%;z-index:999999;padding:20px;transform: translateX(-50%) translateY(0%);font-size: 20px;background: #CEE;max-height:100px;';
	d.innerHTML=msgstr;
	document.body.appendChild(d);
	setTimeout('document.body.removeChild(document.getElementById("'+d.id+'"));',t);
}