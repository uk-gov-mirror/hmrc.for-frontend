var GTMCode =  '';
var raldCustomDimEvent = {};

if(document.getElementById('GTM')) {
    GTMCode = document.getElementById('GTM').getAttribute('GTMCode');
    raldCustomDimEvent[document.getElementById('GTM').getAttribute('startDim')] = document.getElementById('GTM').getAttribute('startDate');
    raldCustomDimEvent[document.getElementById('GTM').getAttribute('returnDim')] = document.getElementById('GTM').getAttribute('timesReturned');
    raldCustomDimEvent[document.getElementById('GTM').getAttribute('refDim')] = document.getElementById('GTM').getAttribute('ref');
    raldCustomDimEvent['event'] = 'RALD01';
}

// Track custom dimensions in GTM
window.dataLayer = window.dataLayer || [];

window.dataLayer.push(raldCustomDimEvent);
// End track custom dimensions in GTM

// Google Tag Manager
(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push(
{'gtm.start': new Date().getTime(),event:'gtm.js'}
);var f=d.getElementsByTagName(s)[0], j=d.createElement(s),dl=l!=='dataLayer'?'&l='+l:'';j.async=true;j.src= 'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f); })(window,document,'script','dataLayer','GTM-'+GTMCode);
// End Google Tag Manager
