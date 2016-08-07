
    $(function() {
    	$('.pie-cache').sparkline('html',  {type: 'pie', sliceColors: ['green','orange'], width: '1.5em', height:'1.5em',
    		 tooltipFormat: '{{offset:names}} {{value}} ({{percent.1}}%)',
    		    tooltipValueLookups: {
    		        'names': {0: "Cached", 1: "No cache"}
    		    } 
    	   });
    	
    	
    	$('.pie-status').sparkline('html',  {type: 'pie', sliceColors: ['green','red', 'orange'], width: '1.5em', height:'1.5em',
   		 tooltipFormat: '{{offset:names}} {{value}} ({{percent.1}}%)',
   		    tooltipValueLookups: {
   		        'names': {0: "Success", 1: "Failure", 2: "Error"}
   		    } 
   	   });
    	
    	
    	setInterval(function(i) {
            $.getJSON('?p=services&json', function(data) {
            	
            	$.each(data,function(i,item){
            		
            		if( item.rate != null ){
            			
            			var graph = $(".graph-rate:nth-child("+i+")");
            			var values = graph.attr("values");
            			values = values + "," + item.rate;
            			graph.attr("values", values	);
            			
            			if(values.split(',').length > 10 ){
            				graph.attr("values", values.substr( values.indexOf(',')+1, values.length));
            			}
            				
            	    	$(".graph-rate:nth-child("+i+")").sparkline("html",  {type: 'bar', chartRangeMin: 0, tooltipFormat: '{{value}} Hits/s',
                		   });
            		}
            	    });
            });
        }, 1000);
    	
    });
