//specify custom path
jasmine.getFixtures().fixturesPath = 'frontend/jasmine/fixtures';

describe("Javascript test suite", function() {

	//startup
	beforeEach(function(){
		loadFixtures('fragment.html');
		//trigger functions
		VoaFor.addFieldMulti();
		VoaFor.removeFieldMulti();
	});

	//teardown
	afterEach(function() {
	  	 $(document).off();
	});

	it('Namespace should be available on the jQuery object', function() {
		expect(VoaCommon, VoaFor, GOVUK, VoaRadioToggle).toBeDefined();
	});

  	it("It contains a spec with an expectation", function() {
  	  expect(true).toBe(true);
  	});

	it("Should be able to set fixtures", function() {
        expect(setFixtures).toBeDefined();
    });


	describe("Add multiple fields", function() {
		it("Should show 1 visible group", function() {
			expect($('.multi-fields-group').length).toEqual(1);
		});
		it("When add more is clicked once should show 2 visible groups", function() {
			$('.add-multi-fields').trigger( "click" );
			expect($('.multi-fields-group').length).toEqual(2);
		});
		it("When add more is clicked twice should show 3 visible groups", function() {
			$('.add-multi-fields').trigger( "click" );
			$('.add-multi-fields').trigger( "click" );
			expect($('.multi-fields-group').length).toEqual(3);
		});
		it("When add more limits are reached, add button should be hidden", function() {
			$('.add-multi-fields').trigger( "click" );
			$('.add-multi-fields').trigger( "click" );
			$('.add-multi-fields').trigger( "click" );
			expect($('.add-multi-fields')).not.toBeVisible();
		});
	});

	describe("Remove multiple fields", function() {
		it("Remove link should be hidden", function() {
			expect($('.multi-fields-group:last .remove-multi-fields')).not.toBeVisible();
		});
		it("When 2 rows are added and remove is clicked on the last row, it should be removed and show 1 visible group", function() {
			$('.add-multi-fields').trigger( "click" );
			expect($('.multi-fields-group').length).toEqual(2);
			$('.multi-fields-group:last .remove-multi-fields').trigger( "click" );
			expect($('.multi-fields-group').length).toEqual(1);
			expect($('.multi-fields-group:last .remove-multi-fields')).not.toBeVisible();
		});
	});

});
