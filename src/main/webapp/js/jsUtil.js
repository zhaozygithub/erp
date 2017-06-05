/********************** Arry ***************/
//根据数据取得再数组中的索引
Array.prototype.getIndex = function(obj){
    for (var i = 0; i < this.length; i++) {
        if (obj == this[i]) {
            return i;
        }
    }
    return -1;
}
//移除数组中的某元素
Array.prototype.remove= function (obj) {
    for (var i = 0; i < this.length; i++) {
        if (obj == this[i]) {
            this.splice(i, 1);
            break;
        }
    }
    return this;
}
//判断元素是否在数组中
Array.prototype.contains= function (obj) {
    for (var i = 0; i < this.length; i++) {
        if (obj == this[i]) {
            return true;
        }
    }
    return false;
}


/********************** String ***************/

String.prototype.startWith=function(str){     
	  var reg=new RegExp("^"+str);     
	  return reg.test(this);        
	}  

String.prototype.endWith=function(str){     
	  var reg=new RegExp(str+"$");     
	  return reg.test(this);        
	}