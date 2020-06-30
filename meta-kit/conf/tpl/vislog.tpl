
#tpl("vislog.7DaysActiveCount")
    {
      "track_total_hits":true,
      "_source": "userid",
      "query": {
        "bool": {
    	  "must" : [
             {"match_phrase": {"userid": #(userid)}}
          ],
          "filter":{
            "range": {
              "vislogid": {
                "gte": #(starttime),
                "lte": #(endtime)
              }
            }
          }
    	}
      },
      "from": 0,"size": 1
    }
#end


