// 最简代码，也就是这些字段必须有
export default {
  path: "/configure",
  meta: {
    title: "配置管理",
    rank: 3
  },
  children: [
    {
      path: "/configure/index",
      name: "key",
      component: () => import("@/views/configure/key/index.vue"),
      meta: {
        title: "openai key管理"
      }
    },
    {
      path: "/fighting/effort",
      name: "chatgpt",
      component: () => import("@/views/configure/chatgpt/index.vue"),
      meta: {
        title: "chatgpt配置"
      }
    }
  ]
};
