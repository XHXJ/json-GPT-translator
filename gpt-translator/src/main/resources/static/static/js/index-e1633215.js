import{ao as o,d as s,r,b as c,e as l,w as p,o as _,g as d,E as i}from"./index-845b918d.js";const u=a=>`/api/${a}`,m=a=>o.request("get",u("translation-data/page"),{params:a}),x=s({__name:"index",setup(a){const e=r({});return m().then(t=>{e.value=t.data}),(t,f)=>{const n=c("el-card");return _(),l(n,null,{default:p(()=>[d(i(e.value),1)]),_:1})}}});export{x as default};
