<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<html>

<head>
	<title>bool: The Online Circuit Builder</title>

	<link rel="icon" 
	type="favicon.ico" 
	href="favicon.ico">

	<script>var pageContext = "${pageContext.request.contextPath}";</script>

    <spring:url value = "/resources/workspace/style/submit.css" var = "submitCSS"/>
    <spring:url value = "/resources/workspace/style/user-menu.css" var = "userMenuCSS"/>
    <spring:url value = "/resources/workspace/style/attribute-editor.css" var = "attributeCSS" />
    <spring:url value = "/resources/workspace/style/workspace.css" var = "workspaceCSS"/>
    <spring:url value = "/resources/workspace/style/toolbar.css" var = "toolbarCSS"/>
    <spring:url value = "/resources/workspace/style/console.css" var = "consoleCSS"/>


    <spring:url value = "/resources/workspace/script/jquery-3.2.0.min.js" var = "jqueryJS"/>
    <spring:url value = "/resources/workspace/script/jquery-ui.min.js" var = "jqueryUI"/>
    <spring:url value = "/resources/workspace/script/kinetic-v5.1.0/kinetic-v5.1.0.js" var = "kineticJS" />
    <spring:url value = "/resources/workspace/script/kinetic-v5.1.0/kinetic-v5.1.0.min.js" var = "kineticMinJS" />

    <spring:url value = "/resources/workspace/script/global.js" var = "globalJS" />
    <spring:url value = "/resources/workspace/script/console.js" var = "consoleJS" />
    <spring:url value = "/resources/workspace/script/components.js" var = "componentsJS" />
    <spring:url value = "/resources/workspace/script/images.js" var = "imagesJS" />
    <spring:url value = "/resources/workspace/script/grid.js" var = "gridJS" />
    <spring:url value = "/resources/workspace/script/submit.js" var = "submitJS" />

    <link href = "${attributeCSS}" rel = "stylesheet" />
    <link href = "${workspaceCSS}" rel = "stylesheet" />
    <link href = "${toolbarCSS}" rel = "stylesheet" />
    <link href = "${consoleCSS}" rel = "stylesheet" />
    <link href = "${submitCSS}" rel = "stylesheet" />
    <link href = "${userMenuCSS}" rel = "stylesheet" />

    <script src="${jqueryJS}"></script>
    <script src="${jqueryUI}"></script>
    <script src="${kineticJS}"></script>
    <script src="${kineticMinJS}"></script>

    <script src="${globalJS}"></script>
    <script src="${consoleJS}"></script>
    <script src="${componentsJS}"></script>
    <script src="${imagesJS}"></script>
    <script src="${gridJS}"></script>
    <script src="${submitJS}"></script>

</head>


<body>

	<div class = "user-menu">
		<div class = "auth">
		</div>
	</div>

	<div class = "toolbar"> <!-- Toolbar -->
		<ul>
			<li class="dropdown">
				<a href="#" class="dropbtn">Authenticate</a>
				<div class="dropdown-content">
		  			<a href="/workspace/toggleLoginLogout">Login/Logout | <c:out value = "${currEmail}"/></a>
		  			<a href="/profile">Exit</a>
				</div>
			</li>
			<li class="dropdown">
				<a href="#" class="dropbtn">File</a>
				<div class="dropdown-content">
		  			<a href="/workspace">New</a>
		  			<a href="#" onclick = "writetofile()">Save</a>
		  			<a href="#">
		  				<label for = "loader" class = "loading">Load</label>
		  				<input id = "loader" type="file">
		  			</a>
		  			<a href = "#" onclick = "promptSubmitMenu()">Submit</a>
		  			<a id="run">Run</a>
		  			<a href="#">Toggle Dynamic Evaluator</a>
				</div>
			</li>
			<li class="dropdown">
			<a href="#" class="dropbtn">Edit</a>
				<div class="dropdown-content">
		  			<a href="#" onclick = "undo()">Undo  ctrl-z</a>
		  			<a href="#" onclick = "redo()">Redo  ctrl-y</a>
		  			<a href="#" onclick = "cut()">Cut   ctrl-x</a>
		  			<a href="#" onclick = "copyToClipBoard()">Copy  ctrl-c</a>
		  			<a href="#" onclick = "pasteToWorkspace()">Paste ctrl-v</a>
		  			<a href="#" onclick = "rotateAxis()">Rotate All</a>
		  			<a href="#" onclick = "rotateSelected()">Rotate Indivually</a>
		  			<a href="#" onclick = "deleteSelected()">Delete Selected</a>
				</div>
			</li>
		</ul>
	</div>

	<div class = "workspace">
		<div class = "grid"> <!-- Grid interface --> 

			<canvas id = "grid-render"></canvas>

		</div>

		<div class = "cmenu"> <!-- Component menu -->

			<div class = "title">Components</div>

			<div class = "twobytwo">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-and.png" id = "AND">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-xor.png" id = "XOR">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-or.png" id = "OR">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-not.png" id = "NOT">
				<!--<img class = "c-icon" src="/resources/workspace/img/cmenu-nand.png" id = "NAND">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-nor.png" id = "NOR">-->
			</div>

			<div class = "onebyone">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-plus.png" id = "CROSS">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-minus.png" id = "I">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-t.png" id = "T">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-l.png" id = "L">
			</div>

			<div class = "twobytwo">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-onbox.png" id = "ON">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-printbox.png" id = "PRINT">
				<img class = "c-icon" src="/resources/workspace/img/cmenu-varbox.png" id = "VAR">
			</div>
			
		</div>

		<div class = "console"> <!-- Console -->

			<div class = "console-top">

				<div class = "console-trigger on" id = "debug-trigger">
					<span class = "console-label">Debug</span>
				</div>

				<div class = "console-trigger" id = "truth-table-trigger">
					<span class = "console-label">Truth Table</span>
				</div>

				<div class = "console-trigger" id = "boolean-trigger">
					<span class = "console-label">Boolean Equations</span>
				</div>

				<div class = "name-container" id = "name-container">		
					<span class = "name-label">Circuit Name</span>
					<input id = "name-input" class = "name-input-textbox" type = "text" name = "circuit-name" value = "">
				</div>

				<div id = "clear-console">Clear Debug</div>

			</div>

			<div class = "console-bottom show" id = "debug">
				<p class = "debug-msg"> Welcome to <i>bool</i>! The Online Circuit Builder</p>
				<div id = "debug-printed">

				</div>
			</div>
			
			<div class = "console-bottom" id = "truth-table">
				<div class = "build-circuit">
					<div class = "bool-button">asdasda</div>
					<div class = "bool-button">asdasads</div>
				</div>
				<div id = "table"></div>
				<div id = "table-buttons"></div>
			</div>

			<div class = "console-bottom" id = "boolean">
				<div class = "build-circuit">
					<div class = "bool-button">asdasda</div>
					<div class = "bool-button">asdasads</div>
				</div>
				<div id = "textbox"></div>
			</div>

		</div>

	</div>

	<div class = "attribute-editor"> <!-- Attribute Editor -->
			<p class = "title">Modify Component Attribute</p>

			<div class = "ae-input" id = "ae-details">
				<p id = "detail-desc"></p>
			</div>

			<div class = "ae-input" id = "ae-delay">		
				<span class = "ae-desc">Delay</span>
				<input id = "ae-delay-text" class = "ae-textbox" type = "text" name = "delay" value = "">
			</div>

			<div class = "ae-input" id = "ae-print">
				<span class = "ae-desc">Message</span>
				<input id = "ae-message-text" class = "ae-textbox" type = "text" name = "print" value = "">		
			</div>

			<div class = "ae-input" id = "ae-label">
				<span class = "ae-desc">Label</span> 
				<input id = "ae-label-text" class = "ae-textbox" type = "text" name = "label" value = "">
			</div>

			<div class = "button-container">	
				<div class = "ae-button" id = "rotate"></div>
				<div class = "ae-button" id = "delete"></div>
				<div class = "ae-button" id = "save"></div>
			</div>
	</div>

	<div class = "blanket"></div>

	<div class = "submit-menu">
		
		<p>Submit Circuit To Profile</p>

		<div id = "div-privacy">
			<form>
				<span class = "desc">Public</span>
				<input type = "radio" class = "privacy" name = "privacy" id = "public" checked = "checked">
				<span class = "desc">Private</span>
				<input type = "radio" class = "privacy" name = "privacy" id = "private">
			</form>
		</div>

		<div id = "div-name">
			<span class = "desc">Name</span>
			<input id = "circuit-name" class = "textbox" type = "text" name = "circuit-name" value = "">				
		</div>

		<div id = "div-share">
			<span class = "desc">Share</span>
			<input id = "circuit-share" class = "textbox" type = "text" name = "circuit-share" value = "">				
		</div>

		<div id = "div-tags">
			<span class = "desc">Tags</span>
			<input id = "circuit-tags" class = "textbox" type = "text" name = "circuit-tags" value = "">				
		</div>

		<div id = "div-submit">
			<button type = "button" onclick = "submitToDatastore()">Submit</button>
			<button type = "button" onclick = "hideSubmitMenu()">Cancel</button>
		</div>
	</div>


    <spring:url value = "/resources/workspace/script/canvas.js" var = "canvas" />
    <spring:url value = "/resources/workspace/script/attribute-editor.js" var = "attributeEditor" />
    <spring:url value = "/resources/workspace/script/selection.js" var = "selection" />
    <spring:url value = "/resources/workspace/script/evaluator.js" var = "evaluator" />
    <spring:url value = "/resources/workspace/script/saving.js" var = "saving" />
    <spring:url value = "/resources/workspace/script/toolbar.js" var = "toolbar" />


    <script src="${canvas}"></script>
    <script src="${attributeEditor}"></script>
    <script src="${selection}"></script>
    <script src="${evaluator}"></script>
    <script src="${saving}"></script>
    <script src="${toolbar}"></script>


</body>

</html> 