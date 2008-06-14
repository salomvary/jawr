JAWR.loader.insert = function(path) {
	for(var x = 0; x < this.jsbundles.length;x++){
		if(this.jsbundles[x].belongsToBundle(path)){
			var bundle =  this.jsbundles[x]
			for(var i = 0; i < bundle.itemPathList.length; i++){
				this.insertScript(bundle.itemPathList[i]);
			}
			//this.insertCondComment(this.jsbundles[x].name,'if IE');
		}
	}
}