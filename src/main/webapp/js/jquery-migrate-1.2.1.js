/*! jQuery Migrate v3.0.1-pre | (c) jQuery Foundation and other contributors | jquery.org/license */
"undefined"==typeof jQuery.migrateMute&&(jQuery.migrateMute=!0),function(a){"function"==typeof define&&define.amd?define(["jquery"],window,a):"object"==typeof module&&module.exports?module.exports=a(require("jquery"),window):a(jQuery,window)}(function(a,b){"use strict";function c(c){var d=b.console;f[c]||(f[c]=!0,a.migrateWarnings.push(c),d&&d.warn&&!a.migrateMute&&(d.warn("JQMIGRATE: "+c),a.migrateTrace&&d.trace&&d.trace()))}function d(a,b,d,e){Object.defineProperty(a,b,{configurable:!0,enumerable:!0,get:function(){return c(e),d},set:function(a){c(e),d=a}})}function e(a,b,d,e){a[b]=function(){return c(e),d.apply(this,arguments)}}a.migrateVersion="3.0.1-pre",function(){var c=/^[12]\./;b.console&&b.console.log&&(a&&!c.test(a.fn.jquery)||b.console.log("JQMIGRATE: jQuery 3.0.0+ REQUIRED"),a.migrateWarnings&&b.console.log("JQMIGRATE: Migrate plugin loaded multiple times"),b.console.log("JQMIGRATE: Migrate is installed"+(a.migrateMute?"":" with logging active")+", version "+a.migrateVersion))}();var f={};a.migrateWarnings=[],void 0===a.migrateTrace&&(a.migrateTrace=!0),a.migrateReset=function(){f={},a.migrateWarnings.length=0},"BackCompat"===b.document.compatMode&&c("jQuery is not compatible with Quirks Mode");var g=a.fn.init,h=a.isNumeric,i=a.find,j=/\[(\s*[-\w]+\s*)([~|^$*]?=)\s*([-\w#]*?#[-\w#]*)\s*\]/,k=/\[(\s*[-\w]+\s*)([~|^$*]?=)\s*([-\w#]*?#[-\w#]*)\s*\]/g;a.fn.init=function(a){var b=Array.prototype.slice.call(arguments);return"string"==typeof a&&"#"===a&&(c("jQuery( '#' ) is not a valid selector"),b[0]=[]),g.apply(this,b)},a.fn.init.prototype=a.fn,a.find=function(a){var d=Array.prototype.slice.call(arguments);if("string"==typeof a&&j.test(a))try{b.document.querySelector(a)}catch(e){a=a.replace(k,function(a,b,c,d){return"["+b+c+'"'+d+'"]'});try{b.document.querySelector(a),c("Attribute selector with '#' must be quoted: "+d[0]),d[0]=a}catch(a){c("Attribute selector with '#' was not fixed: "+d[0])}}return i.apply(this,d)};var l;for(l in i)Object.prototype.hasOwnProperty.call(i,l)&&(a.find[l]=i[l]);a.fn.size=function(){return c("jQuery.fn.size() is deprecated and removed; use the .length property"),this.length},a.parseJSON=function(){return c("jQuery.parseJSON is deprecated; use JSON.parse"),JSON.parse.apply(null,arguments)},a.isNumeric=function(b){function d(b){var c=b&&b.toString();return!a.isArray(b)&&c-parseFloat(c)+1>=0}var e=h(b),f=d(b);return e!==f&&c("jQuery.isNumeric() should not be called on constructed objects"),f},e(a,"holdReady",a.holdReady,"jQuery.holdReady is deprecated"),e(a,"unique",a.uniqueSort,"jQuery.unique is deprecated; use jQuery.uniqueSort"),d(a.expr,"filters",a.expr.pseudos,"jQuery.expr.filters is deprecated; use jQuery.expr.pseudos"),d(a.expr,":",a.expr.pseudos,"jQuery.expr[':'] is deprecated; use jQuery.expr.pseudos");var m=a.ajax;a.ajax=function(){var a=m.apply(this,arguments);return a.promise&&(e(a,"success",a.done,"jQXHR.success is deprecated and removed"),e(a,"error",a.fail,"jQXHR.error is deprecated and removed"),e(a,"complete",a.always,"jQXHR.complete is deprecated and removed")),a};var n=a.fn.removeAttr,o=a.fn.toggleClass,p=/\S+/g;a.fn.removeAttr=function(b){var d=this;return a.each(b.match(p),function(b,e){a.expr.match.bool.test(e)&&(c("jQuery.fn.removeAttr no longer sets boolean properties: "+e),d.prop(e,!1))}),n.apply(this,arguments)},a.fn.toggleClass=function(b){return void 0!==b&&"boolean"!=typeof b?o.apply(this,arguments):(c("jQuery.fn.toggleClass( boolean ) is deprecated"),this.each(function(){var c=this.getAttribute&&this.getAttribute("class")||"";c&&a.data(this,"__className__",c),this.setAttribute&&this.setAttribute("class",c||b===!1?"":a.data(this,"__className__")||"")}))};var q=!1;a.swap&&a.each(["height","width","reliableMarginRight"],function(b,c){var d=a.cssHooks[c]&&a.cssHooks[c].get;d&&(a.cssHooks[c].get=function(){var a;return q=!0,a=d.apply(this,arguments),q=!1,a})}),a.swap=function(a,b,d,e){var f,g,h={};q||c("jQuery.swap() is undocumented and deprecated");for(g in b)h[g]=a.style[g],a.style[g]=b[g];f=d.apply(a,e||[]);for(g in b)a.style[g]=h[g];return f};var r=a.data;a.data=function(b,d,e){var f;if(d&&"object"==typeof d&&2===arguments.length){f=a.hasData(b)&&r.call(this,b);var g={};for(var h in d)h!==a.camelCase(h)?(c("jQuery.data() always sets/gets camelCased names: "+h),f[h]=d[h]):g[h]=d[h];return r.call(this,b,g),d}return d&&"string"==typeof d&&d!==a.camelCase(d)&&(f=a.hasData(b)&&r.call(this,b),f&&d in f)?(c("jQuery.data() always sets/gets camelCased names: "+d),arguments.length>2&&(f[d]=e),f[d]):r.apply(this,arguments)};var s=a.Tween.prototype.run;a.Tween.prototype.run=function(){if(a.easing[this.easing].length>1){c('easing function "jQuery.easing.'+this.easing.toString()+'" should use only first argument');var b=a.easing[this.easing];a.easing[this.easing]=function(c){return b.call(a.easing,c,c,0,1,1)}.bind(this)}s.apply(this,arguments)},a.fx.interval=a.fx.interval||13,b.requestAnimationFrame&&d(a.fx,"interval",a.fx.interval,"jQuery.fx.interval is deprecated");var t=a.fn.load,u=a.event.add,v=a.event.fix;a.event.props=[],a.event.fixHooks={},d(a.event.props,"concat",a.event.props.concat,"jQuery.event.props.concat() is deprecated and removed"),a.event.fix=function(b){var d,e=b.type,f=this.fixHooks[e],g=a.event.props;if(g.length)for(c("jQuery.event.props are deprecated and removed: "+g.join());g.length;)a.event.addProp(g.pop());if(f&&!f._migrated_&&(f._migrated_=!0,c("jQuery.event.fixHooks are deprecated and removed: "+e),(g=f.props)&&g.length))for(;g.length;)a.event.addProp(g.pop());return d=v.call(this,b),f&&f.filter?f.filter(d,b):d},a.event.add=function(a,d){return a===b&&"load"===d&&"complete"===b.document.readyState&&c("jQuery(window).on('load'...) called after load event occurred"),u.apply(this,arguments)},a.each(["load","unload","error"],function(b,d){a.fn[d]=function(){var a=Array.prototype.slice.call(arguments,0);return"load"===d&&"string"==typeof a[0]?t.apply(this,a):(c("jQuery.fn."+d+"() is deprecated"),a.splice(0,0,d),arguments.length?this.on.apply(this,a):(this.triggerHandler.apply(this,a),this))}}),a.each("blur focus focusin focusout resize scroll click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup contextmenu".split(" "),function(b,d){a.fn[d]=function(a,b){return c("jQuery.fn."+d+"() event shorthand is deprecated"),arguments.length>0?this.on(d,null,a,b):this.trigger(d)}}),a(function(){a(b.document).triggerHandler("ready")}),a.event.special.ready={setup:function(){this===b.document&&c("'ready' event is deprecated")}},a.fn.extend({bind:function(a,b,d){return c("jQuery.fn.bind() is deprecated"),this.on(a,null,b,d)},unbind:function(a,b){return c("jQuery.fn.unbind() is deprecated"),this.off(a,null,b)},delegate:function(a,b,d,e){return c("jQuery.fn.delegate() is deprecated"),this.on(b,a,d,e)},undelegate:function(a,b,d){return c("jQuery.fn.undelegate() is deprecated"),1===arguments.length?this.off(a,"**"):this.off(b,a||"**",d)},hover:function(a,b){return c("jQuery.fn.hover() is deprecated"),this.on("mouseenter",a).on("mouseleave",b||a)}});var w=a.fn.offset;a.fn.offset=function(){var d,e=this[0],f={top:0,left:0};return e&&e.nodeType?(d=(e.ownerDocument||b.document).documentElement,a.contains(d,e)?w.apply(this,arguments):(c("jQuery.fn.offset() requires an element connected to a document"),f)):(c("jQuery.fn.offset() requires a valid DOM element"),f)};var x=a.param;a.param=function(b,d){var e=a.ajaxSettings&&a.ajaxSettings.traditional;return void 0===d&&e&&(c("jQuery.param() no longer uses jQuery.ajaxSettings.traditional"),d=e),x.call(this,b,d)};var y=a.fn.andSelf||a.fn.addBack;a.fn.andSelf=function(){return c("jQuery.fn.andSelf() is deprecated and removed, use jQuery.fn.addBack()"),y.apply(this,arguments)};var z=a.Deferred,A=[["resolve","done",a.Callbacks("once memory"),a.Callbacks("once memory"),"resolved"],["reject","fail",a.Callbacks("once memory"),a.Callbacks("once memory"),"rejected"],["notify","progress",a.Callbacks("memory"),a.Callbacks("memory")]];return a.Deferred=function(b){var d=z(),e=d.promise();return d.pipe=e.pipe=function(){var b=arguments;return c("deferred.pipe() is deprecated"),a.Deferred(function(c){a.each(A,function(f,g){var h=a.isFunction(b[f])&&b[f];d[g[1]](function(){var b=h&&h.apply(this,arguments);b&&a.isFunction(b.promise)?b.promise().done(c.resolve).fail(c.reject).progress(c.notify):c[g[0]+"With"](this===e?c.promise():this,h?[b]:arguments)})}),b=null}).promise()},b&&b.call(d,d),d},a});