//	new App.Views.App({ collection: App.contacts })
//	Global app view responsible for creating addContacts and allContacts views
App.Views.App = Backbone.View.extend({
	initialize: function(){
//		Added a listener here for edit view
		vent.on('contact:edit', this.method1, this);
		
//		Create views here
//		var addContactView = new App.Views.AddContact({ collection: App.contacts });	
//		var allContactsView = new App.Views.Contacts({ collection: App.contacts }).render();
		
		$('#allContacts').append(allContactsView.el);
	},
	
//	vent receives a copy of the model from whence it was triggered and it becomes available in the method
	method1: function( contact ){
	}
});