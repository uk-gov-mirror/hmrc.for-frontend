/* overides jshint */
/*******************/
/* jshint -W079 */
/* jshint -W009 */
/* jshint -W098 */
/******************/

// set namespaces (remember to add new namespaces to .jshintrc)
var VoaFor = {};
var VoaCommon = {};
var VoaFeedback = {};
var VoaMessages = {};
var VoaAlerts = {};
var VoaRadioToggle = {};
var ref;

(function ($) {
    'use strict';
    $(document).ready(function () {

        /** Init functions **/
        //voaFor.js
        VoaFor.showHistoryBackLink();
        VoaFor.printPageShouldPrintOnLoad();
        VoaRadioToggle.toggleFieldsBasedOnCheckedRadioButton();
        VoaFor.errorFocus();
        VoaFor.addField();
        VoaFor.removeField();
        VoaFor.addFieldMulti();
        VoaFor.removeFieldMulti();
        VoaFor.selectMobile();
        VoaFor.rentLength();
        VoaFor.updateLabelToggle();
        VoaFor.isEdit();
        VoaFor.getReferrer();
        VoaFor.formatPostcode();
        VoaFor.toggleAgentLeaseContainsRentReviews();
        VoaFor.toggleImage();
        VoaFor.toggleYearsMonths();
        VoaFor.addMultiButtonState();
        VoaFor.timeOutReminder();

        //feedback.js
        VoaFeedback.feedbackOverrides();
        VoaFeedback.toggleHelp();
        VoaFeedback.helpForm();

        //intelAlerts.js
        VoaAlerts.intelAlert();

        //radioToggle.js
        VoaRadioToggle.radioDataShowField();
        VoaRadioToggle.radioDataShowFields();
        
        //common.js
        VoaCommon.GdsSelectionButtons();
        VoaCommon.linkShowManualAddress();
        VoaCommon.addErrorAnchors();
        VoaCommon.anchorFocus();
        VoaCommon.details();
        VoaCommon.characterCount();
        VoaCommon.stickyFooter();
        VoaCommon.setGdsClasses();
    });

})(jQuery);