<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />		
		<title>咨询详情</title>
		
		   <script type="text/javascript">
    	
   		document.addEventListener('plusready', function(){
   			//console.log("所有plus api都应该在此事件发生后调用，否则会出现plus is undefined。"
   			
   		});
   		
    //实现真实一像素显示
//		    function getOnePx(){
//		        var dpi=window.devicePixelRatio,//获取屏幕像素比
//		            scalez=1/dpi;
//		        document.write('<meta content="width=device-width,initial-scale='+scalez+',maximum-scale='+scalez+',user-scalable=no" name="viewport">')//通过meta来实现缩放像素比的倍数
//		    }
    //适配函数，动态修改html节点字号
		    function reset(){
		        var htmlo=document.getElementsByTagName('html')[0],
		            clientW=document.documentElement.clientWidth || document.body.clientWidth,
		            fontSz=clientW/6.4+'px';
		        htmlo.style.fontSize=fontSz;
		         
		    }
    //调用一像素显示
   			// getOnePx();
    //初始进来执行一次适配
    			reset();
    //当屏幕旋转的时候，再次执行一次适配
		    window.addEventListener('resize',function(){
		        setTimeout(function(){reset();},100);
		    },false)
    </script>
		<style>
			html,
			body,
			div,
			span,
			applet,
			object,
			iframe,
			h1,
			h2,
			h3,
			h4,
			h5,
			h6,
			p,
			blockquote,
			pre,
			a,
			abbr,
			acronym,
			address,
			big,
			cite,
			code,
			del,
			dfn,
			em,
			font,
			img,
			ins,
			kbd,
			q,
			s,
			samp,
			small,
			strike,
			strong,
			sub,
			sup,
			tt,
			var,
			dd,
			dl,
			dt,
			li,
			ol,
			ul,
			fieldset,
			form,
			label,
			legend {
			    margin: 0;
			    padding: 0;
			    border: 0;
			    outline: 0;
			}
			img {
			    border: none;
			}
			p {
			    margin-bottom: 0;
			}
			body,
			a,
			input,
			button {
			    font-family: "Microsoft Yahei", "Droid Sans Fallback", "Arial", "Helvetica", "sans-serif", "宋体";
			    font-size: 14px;
			    outline: none;
			    color: #666;
			    text-decoration: none;
			}
			
			ul {
			    list-style-type: none;
			    padding: 0;
			    margin: 0;
			}
			
			a {
			    color: #428bca;
			}
			
			a:hover {
			    text-decoration: none;
			}
			
			table {
			    text-align: center;
			    vertical-align: middle;
			}
			
			th {
			    text-align: center;
			    vertical-align: middle;
			}
			
			.biaoti{
				width: 100%;
				height: auto;
				font-size: 0.4rem;
				color: #000000;
				line-height: 0.6rem;
				
				text-align: left;
				padding: 0.2rem;	
				box-sizing: border-box;	
			}
			.gupiao{
				padding-left: 0.36rem;
				width: auto;
				color: #cacaca;
				font-size: 0.17rem;
			}
			.time{
				margin-left: 0.1rem;
				width: auto;
				color: #cacaca;
				font-size: 0.17rem;
			}
			
			.content{
				width: 100%;
				height: auto;	
				padding: 0.3rem;	
				box-sizing: border-box;		
				color: #000000;
				font-size: 0.2rem;
				line-height: 0.5rem;
			}
			
			.content img{
				width: 100%;
				height: auto;
			}
			.lanse{
				color: #1726ea;
			}
		</style>
	</head>
	<body>
		<div class="biaoti">
			${title}
		</div>
		<span class="gupiao">
			
		</span>
		<span class="time">
			${time}
		</span>
		<div class="content">
			${content}
		</div>		
		
		
	
	</body>
</html>