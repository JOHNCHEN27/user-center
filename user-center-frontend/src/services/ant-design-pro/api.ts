// @ts-ignore
/* eslint-disable */
import request from '@/plugins/globalRequest';

/** 获取当前的用户 GET /api/user/current */
export async function currentUser(options?: { [key: string]: any }) {
  return  request<API.BaseResponse<API.CurrentUser>>('/api/user/current', {
    method: 'GET',
    ...(options || {}),
  });

  //从localStorage取出token，判断token是否存在
  // let token = localStorage.getItem('token')
  // if(token == null){
  //   const response = await fetch('/api/user/islogin', {
  //     method: 'GET',
  //     ...(options || {}),
  //   });
  //   //const data = await response.json(); // 从响应中提取JSON数据
  //   const headers = response.headers; // 获取响应头信息
  //
  //   // 在这里处理响应头信息 获取 token
  //   token = headers.get('authorization');
  //   console.log('Content-Type:', token);
  //
  //   //过期时间设置为两小时
  //   const currentTime = new Date();
  //   const expirationTime = new Date(currentTime.getTime() + 2 * 60 * 60 * 1000);
  //   //存到本地缓存中
  //   localStorage.setItem('token',token);
  //   localStorage.setItem('tokenExpiration',expirationTime)
  // }
  //
  // // 在需要的时候，检查token是否过期
  // const tokenExpiration = new Date(localStorage.getItem('tokenExpiration'));
  // if (new Date() > tokenExpiration ){
  //   //token已经过期 清除缓存过期时间 直接返回null
  //   localStorage.removeItem("tokenExpiration");
  //   return null;
  // } else{
  //   //token未过期
  //   return token; // 返回从后端服务获取的数据
  // }



}

/** 退出登录接口 POST /api/user/logout */
export async function outLogin(options?: { [key: string]: any }) {
  return request<API.BaseResponse<number>>('/api/user/logout', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 登录接口 POST /api/user/login */
export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  return request<API.BaseResponse<API.LoginResult>>('/api/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 注册接口 POST /api/user/register */
export async function register(body: API.RegisterParams, options?: { [key: string]: any }) {
  return request<API.BaseResponse<API.RegisterResult>>('/api/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 搜索用户 GET /api/user/search */
export async function searchUsers(options?: { [key: string]: any }) {
  return request<API.BaseResponse<API.CurrentUser[]>>('/api/user/search', {
    method: 'GET',
    ...(options || {}),
  });
}


/** 此处后端没有提供注释 GET /api/notices */
export async function getNotices(options?: { [key: string]: any }) {
  return request<API.NoticeIconList>('/api/notices', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取规则列表 GET /api/rule */
export async function rule(
  params: {
    // query
    /** 当前的页码 */
    current?: number;
    /** 页面的容量 */
    pageSize?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.RuleList>('/api/rule', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 新建规则 PUT /api/rule */
export async function updateRule(options?: { [key: string]: any }) {
  return request<API.RuleListItem>('/api/rule', {
    method: 'PUT',
    ...(options || {}),
  });
}

/** 新建规则 POST /api/rule */
export async function addRule(options?: { [key: string]: any }) {
  return request<API.RuleListItem>('/api/rule', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 删除规则 DELETE /api/rule */
export async function removeRule(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/api/rule', {
    method: 'DELETE',
    ...(options || {}),
  });
}
