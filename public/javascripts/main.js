/* create the namespace within the self invoking anonymous function of main, called first
 Then add classes and instances to that namespace by non self invoking fucntions that get called once they are used
 */

(function(){
	window.App = {
		Models: {},
		Collections: {},
		Views: {},
		Router: {}
	};
	
	window.vent = _.extend({}, Backbone.Events);
	
	window.template = function( id ){
		return _.template( $('#'+id).html() );
	}
})(); 