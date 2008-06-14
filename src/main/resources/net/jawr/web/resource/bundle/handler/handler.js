if(!window.JAWR) 
	JAWR = {};
JAWR.loader = {
	head : document.getElementsByTagName('head')[0],
	insert : function(path){
		for(var x = 0; x < this.jsbundles.length;x++){
				if(this.jsbundles[x].belongsToBundle(path)){
					this.insertScript(this.jsbundles[x].prefix + this.jsbundles[x].name);
					//this.insertCondComment(this.jsbundles[x].name,'if IE');
				}
			}
	},
	insertScript : function(path){
			var script = document.createElement("script");
			script.src = this.normalizePath(this.mapping + path);
			script.type = "text/javascript";
			JAWR.loader.head.appendChild(script);
	},
	insertCondComment : function(path,condition){
	 path = this.normalizePath(this.mapping + path);
	 document.write('<!--[' + condition + ']>\n<script type="text/javascript" src="'+path+'"><\/script><![endif]-->\n');
	},
	normalizePath : function(path) {
		return path.replace('//','/');
	} 	
}
JAWR.ResourceBundle = function(name, prefix, itemPathList){this.name = name;this.prefix = prefix;this.itemPathList = itemPathList;}
JAWR.ResourceBundle.prototype.belongsToBundle = function(path) {	
	if(path == this.name)
		return true;
	for(var x = 0; x < this.itemPathList.length; x++) {
		if(this.itemPathList[x] == path)
			return true;
	}
	return false;
}
