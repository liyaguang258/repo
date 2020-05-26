<#if templateid == 'VISLOG_ALL_QUERY'>
{
  "query": {
    "bool": {

    }
  },
  "sort":[
    {"createtime" : "desc"}
  ],
  "from": ${from} ,"size": #{size}
}
<#elseif templateid == 'PVLOG_ALL_QUERY'>
{
  "query": {
    "bool": {
        "must" : [
            <#if link_from??>
            {"match_phrase": {"link_from": "${link_from}"}}
            </#if>
            <#if cate??>
            {"match_phrase": {"cate": "${cate}"}}
            </#if>
        ]
    }
  },
  "sort":[
    {"createtime" : "desc"}
  ],
  "from": ${from} ,"size": #{size}
}
</#if>