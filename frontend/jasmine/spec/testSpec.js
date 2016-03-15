//specify custom path
jasmine.getFixtures().fixturesPath = 'frontend/jasmine/fixtures';

describe("Javascript test suite", function() {

	//startup
	beforeEach(function(){
		loadFixtures('fragment.html');
		//trigger functions
        VoaCommon.radioDataShowField();
		VoaCommon.radioDataShowFields();
		VoaFor.addFieldMulti();
		VoaFor.removeFieldMulti();
		VoaCommon.addAnchors();
	});

	//teardown
	afterEach(function() {
	  	 $(document).off();
	});

	it('Namespace should be available on the jQuery object', function() {
		expect(VoaCommon, VoaFor, GOVUK).toBeDefined();
	});

  	it("It contains a spec with an expectation", function() {
  	  expect(true).toBe(true);
  	});

	it("Should be able to set fixtures", function() {
        expect(setFixtures).toBeDefined();
    });

	describe("Radio show/hide fields", function() {
		it("Target group should not be visible", function() {
			expect($('.question0')).toHaveClass('hidden');
			expect($('.question0')).not.toBeVisible();
	    });
		it("Given \"Show element\" is checked target group should visible", function() {
			$('#question0_false').prop('checked', true).change();
			expect($('.question0')).not.toHaveClass('hidden');
			expect($('.question0')).toBeVisible();
	    });
		it("Given \"Hide element\" is checked target group should not be visible", function() {
			$('#question0_true').prop('checked', true).change();
			expect($('.question0')).toHaveClass('hidden');
			expect($('.question0')).not.toBeVisible();
	    });
    });

	describe("Radio show/hide multiple fields", function() {
		it("Target groups should not be visible", function() {
			expect($('#question3_input, #question4_input')).not.toBeVisible();
	    });
		it("Given \"Show multiple elements\" is checked target group should visible", function() {
			$('#question2_show').prop('checked', true).change();
			expect($('#question3_input, #question4_input')).toBeVisible();
	    });
		it("Given \"Hide multiple elements\" is checked target group should not be visible", function() {
			$('#question2_hide').prop('checked', true).change();
			expect($('#question3_input, #question4_input')).not.toBeVisible();
		});

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

	describe("Add anchors", function() {
		it("Should add anchors to each fieldset", function() {
			$('#radioFrag fieldset span').each(function(){
				expect($(this).attr('id').indexOf('anchor')).not.toEqual(-1);
				expect($(this).attr('id').indexOf('anchor')).toEqual(10);
			});
		});
	});

});
