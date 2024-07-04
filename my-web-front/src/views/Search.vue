<template>
  <div>
    <div id="nav-bottom">
      <!--顶部-->

      <div class="nav-top">
        <div class="top">
          <div class="py-container">
            <div class="shortcut">
              <ul class="fl">
                <li class="f-item">我的商城 欢迎您！</li>
                <li v-if="username == '' || username == null" class="f-item">
                  <span>
                    请<a href="http://localhost:8080/cas/logout?service=http://localhost:8080/"> 登录</a>
                  </span>
                  <span>
                    <a
                      href="http://localhost:8080/user/register"
                    >免费注册</a>
                  </span>
                </li>
                <li v-else class="f-item">
                  <span>
                    {{ username }}
                    <a
                      href="javascript:void(0)"
                      @click="logout"
                    >退出登录</a>
                  </span>
                </li>
              </ul>
              <div class="fr typelist">
                <ul class="types">
                  <li class="f-item">
                    <span><a v-if="username" :href="orderUrl">我的订单</a></span>
                  </li>
                  <li class="f-item">
                    <span><a
                      href="http://localhost:8080/cart.html"
                    >我的购物车</a></span>
                  </li>
                  <li class="f-item"><span>我的商城</span></li>
                  <li class="f-item"><span>商城会员</span></li>
                  <li class="f-item"><span>企业采购</span></li>
                  <li class="f-item"><span>关注商城</span></li>
                  <li class="f-item"><span>网站导航</span></li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <!--头部-->
        <div class="header">
          <div class="py-container">
            <div class="yui3-g Logo">
              <div class="yui3-u Left logoArea">
                <a
                  class="logo-bd"
                  title="我的商城"
                  href="http://localhost:8080/"
                  target="_blank"
                />
              </div>
              <div class="yui3-u Rit searchArea">
                <div class="search">
                  <div class="sui-form form-inline">
                    <!--searchAutoComplete-->
                    <div class="input-append">
                      <input
                        id="autocomplete"
                        v-model="searchKeyWrd"
                        type="text"
                        name="keywords"
                        class="input-error input-xxlarge"
                      >

                      <button
                        class="sui-btn btn-xlarge btn-danger"
                        @click="
                          getSearch('search?keywords=' + searchKeyWrd)
                        "
                      >
                        搜索
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 商品分类导航 -->
    <div class="typeNav">
      <div class="py-container">
        <div class="yui3-g NavList">
          <div class="all-sorts-list">
            <div class="yui3-u Left all-sort">
              <h4>全部商品分类</h4>
            </div>
          </div>
          <div class="yui3-u Center navArea">
            <ul class="nav">
              <li v-for="(item, index) in indexCategoryList" :key="index" class="f-item" style="cursor: pointer" @click="getSearch('search?keywords=' + item)">{{ item }}</li>
              <li class="f-item">
                <a
                  href="http://localhost:8080/seckill-index.html"
                  target="_blank"
                >秒杀</a>
              </li>
            </ul>
          </div>
          <div class="yui3-u Right" />
        </div>
      </div>
    </div>
    <!--list-content-->
    <div class="main">
      <div class="py-container">
        <!--bread-->
        <div class="bread">
          <ul class="fl sui-breadcrumb">
            <li>
              <a href="#">条件：</a>
            </li>
          </ul>
          <ul class="fl sui-tag">
            <li
              v-if="searchMap.hasOwnProperty('category') == true"
              class="with-x"
            >
              <span>分类：{{ searchMap.category }}</span>
              <i>
                <a
                  @click="
                    getSearch(
                      url.replace('&category=' + searchMap.category, '')
                    )
                  "
                >×</a>
              </i>
            </li>

            <!--品牌-->
            <li v-if="searchMap.hasOwnProperty('brand') == true" class="with-x">
              <span> 品牌：{{ searchMap.brand }}</span>
              <i><a
                @click="
                  getSearch(url.replace('&brand=' + searchMap.brand, ''))
                "
              >×</a></i>
            </li>

            <!--规格-->
            <!--          <li class="with-x"  v-for="(value,key,i) in searchMap" :key="i"   >-->
            <!--            <span>{{key.substr(5)}}+{{value}}</span>-->
            <!--            <i><a  @click="getSearch(url.replace('&'+key+ '='+value ,''))">×</a></i>-->
            <!--          </li>-->

            <!--价格-->
            <li v-if="searchMap.hasOwnProperty('price') == true" class="with-x">
              <span>价格：{{ searchMap.price }}</span>
              <i><a
                @click="
                  getSearch(url.replace('&price=' + searchMap.price, ''))
                "
              >×</a></i>
            </li>
          </ul>
        </div>
        <!--selector-->
        <div class="clearfix selector">
          <div
            v-if="searchMap.hasOwnProperty('category') == false"
            class="type-wrap"
          >
            <div class="fl key">商品分类</div>
            <div
              v-for="(item, index) in result.categoryList"
              :key="index"
              class="fl value"
            >
              <span>
                <a @click="getSearch(url + '&category=' + item)">{{ item }}</a>
              </span>
            </div>
            <div class="fl ext" />
          </div>
          <div
            v-if="
              searchMap.hasOwnProperty('brand') == false &&
                searchMap.hasOwnProperty('brandList') == false
            "
            class="type-wrap logo"
          >
            <div class="fl key brand">品牌</div>
            <div class="value logos">
              <ul class="logo-list">
                <li v-for="(brand, index) in result.brandList" :key="index">
                  <a @click="getSearch(url + '&brand=' + brand.name)">
                    <img v-if="brand.image != ''" :src="brand.image">
                    <span v-if="brand.image == ''">{{ brand.name }}</span>
                  </a>
                </li>
              </ul>
            </div>
            <div class="ext" />
          </div>
          <div
            v-for="(spec, index) in result.specList"
            :key="index"
            class="type-wrap"
          >
            <div v-if="searchMap.hasOwnProperty('spec.' + spec.name) == false">
              <div class="fl key">{{ spec.name }}</div>
              <div class="fl value">
                <ul class="type-list">
                  <li v-for="(option, idx) in spec.options" :key="idx">
                    <a
                      :text="option"
                      @click="
                        getSearch(url + '&spec.' + spec.name + '=' + option)
                      "
                    />
                  </li>
                </ul>
              </div>
              <div class="fl ext" />
            </div>
          </div>
          <div
            v-if="searchMap.hasOwnProperty('price') == false"
            class="type-wrap"
          >
            <div class="fl key">价格</div>
            <div class="fl value">
              <ul class="type-list">
                <li>
                  <a @click="getSearch(url + '&price=0-500')">0-500元</a>
                </li>
                <li>
                  <a @click="getSearch(url + '&price=500-1000')">500-1000元</a>
                </li>
                <li>
                  <a
                    @click="getSearch(url + '&price=1000-1500')"
                  >1000-1500元</a>
                </li>
                <li>
                  <a
                    @click="getSearch(url + '&price=1500-2000')"
                  >1500-2000元</a>
                </li>
                <li>
                  <a
                    @click="getSearch(url + '&price=2000-3000')"
                  >2000-3000元</a>
                </li>
                <li>
                  <a @click="getSearch(url + '&price=3000-*|')">3000元以上</a>
                </li>
              </ul>
            </div>
            <div class="fl ext" />
          </div>

          <!--details-->
          <div class="details">
            <div class="sui-navbar">
              <div class="navbar-inner filter">
                <ul class="sui-nav">
                  <li :class="searchMap.sortField == '' ? 'active' : ''">
                    <a
                      @click="
                        getSearch(
                          url
                            .replace('&sortRule=' + searchMap.sortRule, '')
                            .replace('&sortField=' + searchMap.sortField, '')
                        )
                      "
                    >综合</a>
                  </li>
                  <li :class="searchMap.sortField == 'saleNum' ? 'active' : ''">
                    <a
                      @click="
                        getSearch(
                          url
                            .replace('&sortRule=' + searchMap.sortRule, '')
                            .replace('&sortField=' + searchMap.sortField, '') +
                            '&sortRule=DESC&sortField=saleNum'
                        )
                      "
                    >销量</a>
                  </li>
                  <li
                    :class="searchMap.sortField == 'createTime' ? 'active' : ''"
                  >
                    <a
                      @click="
                        getSearch(
                          url
                            .replace('&sortRule=' + searchMap.sortRule, '')
                            .replace('&sortField=' + searchMap.sortField, '') +
                            '&sortRule=DESC&sortField=createTime'
                        )
                      "
                    >新品</a>
                  </li>
                  <li
                    :class="searchMap.sortField == 'commentNum' ? 'active' : ''"
                  >
                    <a
                      @click="
                        getSearch(
                          url
                            .replace('&sortRule=' + searchMap.sortRule, '')
                            .replace('&sortField=' + searchMap.sortField, '') +
                            '&sortRule=DESC&sortField=commentNum'
                        )
                      "
                    >评价</a>
                  </li>
                  <li
                    :class="
                      searchMap.sortField == 'price' &&
                        searchMap.sortRule == 'ASC'
                        ? 'active'
                        : ''
                    "
                  >
                    <a
                      @click="
                        getSearch(
                          url
                            .replace('&sortRule=' + searchMap.sortRule, '')
                            .replace('&sortField=' + searchMap.sortField, '') +
                            '&sortRule=ASC&sortField=price'
                        )
                      "
                    >价格⬆</a>
                  </li>
                  <li
                    :class="
                      searchMap.sortField == 'price' &&
                        searchMap.sortRule == 'DESC'
                        ? 'active'
                        : ''
                    "
                  >
                    <a
                      @click="
                        getSearch(
                          url
                            .replace('&sortRule=' + searchMap.sortRule, '')
                            .replace('&sortField=' + searchMap.sortField, '') +
                            '&sortRule=DESC&sortField=price'
                        )
                      "
                    >价格⬇</a>
                  </li>
                </ul>
              </div>
            </div>

            <div class="goods-list">
              <ul
                v-if="result.rows.length != 0"
                class="yui3-g"
                style="display: flex; flex-wrap: wrap"
              >
                <li
                  v-for="(sku, index) in result.rows"
                  :key="index"
                  class="yui3-u-1-5"
                >
                  <div class="list-wrap">
                    <div class="p-img">
                      <a
                        :href="`/html/${sku.skuId}.html?ticket=${ticket}&username=${username}`"
                        target="_blank"
                      ><img
                        :src="sku.image"
                      ></a>
                    </div>
                    <div class="price">
                      <strong>
                        <em>¥</em>
                        <i>{{ sku.price / 100 }}.00</i>
                      </strong>
                    </div>
                    <div class="attr">
                      <em v-html="sku.name" />
                    </div>
                    <div class="operate">
                      <a
                        href="javascript:void(0)"
                        class="sui-btn btn-bordered btn-danger"
                        @click="toCar(sku.skuId)"
                      >加入购物车</a>
                      <!-- <a href="javascript:void(0);" class="sui-btn btn-bordered"
                      >收藏</a
                    > -->
                    </div>
                  </div>
                </li>
              </ul>
              <ul v-else-if="result.rows.length == 0" class="yui3-g" />
            </div>
            <div class="fr page">
              <div class="sui-pagination pagination-large">
                <ul>
                  <li v-if="pageNo <= 1" class="prev disabled">
                    <a>«</a>
                  </li>
                  <li v-if="pageNo > 1" class="prev">
                    <a
                      @click="
                        getSearch(
                          url.replace(
                            '&pageNo=' + pageNo,
                            '&pageNo=' + (pageNo - 1)
                          )
                        )
                      "
                    >«</a>
                  </li>

                  <li v-if="startPage > 1" class="dotted">
                    <span>...</span>
                  </li>
                  <li
                    v-for="(page, index) in endPage"
                    :key="index"
                    :class="page == pageNo ? 'active' : ''"
                  >
                    <a
                      @click="
                        getSearch(
                          url.replace(
                            '&pageNo=' + pageNo,
                            '&pageNo=' + page
                          )
                        )
                      "
                    >
                      {{ page }}</a>
                  </li>
                  <li
                    v-if="endPage < result.totalPages"
                    class="dotted"
                  >
                    <span>...</span>
                  </li>

                  <li v-if="pageNo < result.totalPages" class="next">
                    <a
                      @click="
                        getSearch(
                          url.replace(
                            '&pageNo=' + pageNo,
                            '&pageNo=' + (pageNo + 1)
                          )
                        )
                      "
                    >»</a>
                  </li>
                  <li
                    v-if="pageNo >= result.totalPages"
                    class="next disabled"
                  >
                    <a>»</a>
                  </li>
                </ul>
                <div>
                  <span v-text="'共' + result.totalPages + '页'" />
                </div>
                <!--<div><span>共10页&nbsp;</span><span>
      到第
      <input type="text" class="page-num">
      页 <button class="page-confirm" onclick="alert(1)">确定</button></span></div>-->
              </div>
            </div>
          </div>
          <!--hotsale-->
          <div class="clearfix hot-sale">
            <h4 class="title">热卖商品</h4>
            <div class="hot-list">
              <ul class="yui3-g">
                <li class="yui3-u-1-4">
                  <div class="list-wrap">
                    <div class="p-img">
                      <img src="../assets/img/like_01.png">
                    </div>
                    <div class="attr">
                      <em>Apple苹果iPhone 6s (A1699)</em>
                    </div>
                    <div class="price">
                      <strong>
                        <em>¥</em>
                        <i>4088.00</i>
                      </strong>
                    </div>
                    <div class="commit">
                      <i class="command">已有700人评价</i>
                    </div>
                  </div>
                </li>
                <li class="yui3-u-1-4">
                  <div class="list-wrap">
                    <div class="p-img">
                      <img src="../assets/img/like_03.png">
                    </div>
                    <div class="attr">
                      <em>金属A面，360°翻转，APP下单省300！</em>
                    </div>
                    <div class="price">
                      <strong>
                        <em>¥</em>
                        <i>4088.00</i>
                      </strong>
                    </div>
                    <div class="commit">
                      <i class="command">已有700人评价</i>
                    </div>
                  </div>
                </li>
                <li class="yui3-u-1-4">
                  <div class="list-wrap">
                    <div class="p-img">
                      <img src="../assets/img/like_04.png">
                    </div>
                    <div class="attr">
                      <em>256SSD商务大咖，完爆职场，APP下单立减200</em>
                    </div>
                    <div class="price">
                      <strong>
                        <em>¥</em>
                        <i>4068.00</i>
                      </strong>
                    </div>
                    <div class="commit">
                      <i class="command">已有20人评价</i>
                    </div>
                  </div>
                </li>
                <li class="yui3-u-1-4">
                  <div class="list-wrap">
                    <div class="p-img">
                      <img src="../assets/img/like_02.png">
                    </div>
                    <div class="attr">
                      <em>Apple苹果iPhone 6s (A1699)</em>
                    </div>
                    <div class="price">
                      <strong>
                        <em>¥</em>
                        <i>4088.00</i>
                      </strong>
                    </div>
                    <div class="commit">
                      <i class="command">已有700人评价</i>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 底部栏位 -->
    <!--页面底部-->
    <div class="clearfix footer">
      <div class="py-container">
        <div class="footlink">
          <div class="Mod-service">
            <ul class="Mod-Service-list">
              <li class="grid-service-item intro intro1">
                <i class="serivce-item fl" />
                <div class="service-text">
                  <h4>正品保障</h4>
                  <p>正品保障，提供发票</p>
                </div>
              </li>
              <li class="grid-service-item intro intro2">
                <i class="serivce-item fl" />
                <div class="service-text">
                  <h4>正品保障</h4>
                  <p>正品保障，提供发票</p>
                </div>
              </li>
              <li class="grid-service-item intro intro3">
                <i class="serivce-item fl" />
                <div class="service-text">
                  <h4>正品保障</h4>
                  <p>正品保障，提供发票</p>
                </div>
              </li>
              <li class="grid-service-item intro intro4">
                <i class="serivce-item fl" />
                <div class="service-text">
                  <h4>正品保障</h4>
                  <p>正品保障，提供发票</p>
                </div>
              </li>
              <li class="grid-service-item intro intro5">
                <i class="serivce-item fl" />
                <div class="service-text">
                  <h4>正品保障</h4>
                  <p>正品保障，提供发票</p>
                </div>
              </li>
            </ul>
          </div>
          <div class="clearfix Mod-list">
            <div class="yui3-g">
              <div class="yui3-u-1-6">
                <h4>购物指南</h4>
                <ul class="unstyled">
                  <li>购物流程</li>
                  <li>会员介绍</li>
                  <li>生活旅行/团购</li>
                  <li>常见问题</li>
                  <li>购物指南</li>
                </ul>
              </div>
              <div class="yui3-u-1-6">
                <h4>配送方式</h4>
                <ul class="unstyled">
                  <li>上门自提</li>
                  <li>211限时达</li>
                  <li>配送服务查询</li>
                  <li>配送费收取标准</li>
                  <li>海外配送</li>
                </ul>
              </div>
              <div class="yui3-u-1-6">
                <h4>支付方式</h4>
                <ul class="unstyled">
                  <li>货到付款</li>
                  <li>在线支付</li>
                  <li>分期付款</li>
                  <li>邮局汇款</li>
                  <li>公司转账</li>
                </ul>
              </div>
              <div class="yui3-u-1-6">
                <h4>售后服务</h4>
                <ul class="unstyled">
                  <li>售后政策</li>
                  <li>价格保护</li>
                  <li>退款说明</li>
                  <li>返修/退换货</li>
                  <li>取消订单</li>
                </ul>
              </div>
              <div class="yui3-u-1-6">
                <h4>特色服务</h4>
                <ul class="unstyled">
                  <li>夺宝岛</li>
                  <li>DIY装机</li>
                  <li>延保服务</li>
                  <li>我的商城卡</li>
                  <li>我的商城通信</li>
                </ul>
              </div>
              <div class="yui3-u-1-6">
                <h4>帮助中心</h4>
                <img src="../assets/img/wx_cz.jpg">
              </div>
            </div>
          </div>
          <div class="Mod-copyright">
            <ul class="helpLink">
              <li>关于我们<span class="space" /></li>
              <li>联系我们<span class="space" /></li>
              <li>关于我们<span class="space" /></li>
              <li>商家入驻<span class="space" /></li>
              <li>营销中心<span class="space" /></li>
              <li>友情链接<span class="space" /></li>
              <li>关于我们<span class="space" /></li>
              <li>营销中心<span class="space" /></li>
              <li>友情链接<span class="space" /></li>
              <li>关于我们</li>
            </ul>
            <p>地址：**********</p>
            <p>
              **********公司 | 京**********号 Copyright © 2022-2025
            </p>
          </div>
        </div>
      </div>
    </div>
    <!--页面底部END-->

    <!-- 基础js库 -->

    <!--侧栏面板开始-->
    <div class="J-global-toolbar">
      <div class="toolbar-wrap J-wrap">
        <div class="toolbar">
          <div class="toolbar-panels J-panel">
            <!-- 购物车 -->
            <div
              style="visibility: hidden"
              class="
                J-content
                toolbar-panel
                tbar-panel-cart
                toolbar-animate-out
              "
            >
              <h3 class="tbar-panel-header J-panel-header">
                <a
                  href=""
                  class="title"
                ><i /><em class="title">购物车</em></a>
                <span
                  class="close-panel J-close"
                  onclick="cartPanelView.tbar_panel_close('cart');"
                />
              </h3>
              <div class="tbar-panel-main">
                <div class="tbar-panel-content J-panel-content">
                  <div id="J-cart-tips" class="tbar-tipbox hide">
                    <div class="tip-inner">
                      <span
                        class="tip-text"
                      >还没有登录，登录后商品将被保存</span>
                      <a href="#none" class="tip-btn J-login">登录</a>
                    </div>
                  </div>
                  <div id="J-cart-render">
                    <!-- 列表 -->
                    <div id="cart-list" class="tbar-cart-list" />
                  </div>
                </div>
              </div>
              <!-- 小计 -->
              <div id="cart-footer" class="tbar-panel-footer J-panel-footer">
                <div class="tbar-checkout">
                  <div class="jtc-number">
                    <strong id="cart-number" class="J-count">0</strong>件商品
                  </div>
                  <div class="jtc-sum">
                    共计：<strong id="cart-sum" class="J-total">¥0</strong>
                  </div>
                  <a
                    class="jtc-btn J-btn"
                    href="#none"
                    target="_blank"
                  >去购物车结算</a>
                </div>
              </div>
            </div>

            <!-- 我的关注 -->
            <div
              style="visibility: hidden"
              data-name="follow"
              class="J-content toolbar-panel tbar-panel-follow"
            >
              <h3 class="tbar-panel-header J-panel-header">
                <a href="#" target="_blank" class="title">
                  <i /> <em class="title">我的关注</em>
                </a>
                <span
                  class="close-panel J-close"
                  onclick="cartPanelView.tbar_panel_close('follow');"
                />
              </h3>
              <div class="tbar-panel-main">
                <div class="tbar-panel-content J-panel-content">
                  <div class="tbar-tipbox2">
                    <div class="tip-inner"><i class="i-loading" /></div>
                  </div>
                </div>
              </div>
              <div class="tbar-panel-footer J-panel-footer" />
            </div>

            <!-- 我的足迹 -->
            <div
              style="visibility: hidden"
              class="
                J-content
                toolbar-panel
                tbar-panel-history
                toolbar-animate-in
              "
            >
              <h3 class="tbar-panel-header J-panel-header">
                <a href="#" target="_blank" class="title">
                  <i /> <em class="title">我的足迹</em>
                </a>
                <span
                  class="close-panel J-close"
                  onclick="cartPanelView.tbar_panel_close('history');"
                />
              </h3>
              <div class="tbar-panel-main">
                <div class="tbar-panel-content J-panel-content">
                  <div class="jt-history-wrap">
                    <ul>
                      <!--<li class="jth-item">
                      <a href="#" class="img-wrap"> <img src="../../.../portal/img/like_03.png" height="100" width="100" /> </a>
                      <a class="add-cart-button" href="#" target="_blank">加入购物车</a>
                      <a href="#" target="_blank" class="price">￥498.00</a>
                    </li>
                    <li class="jth-item">
                      <a href="#" class="img-wrap"> <img src="../../../portal/img/like_02.png" height="100" width="100" /></a>
                      <a class="add-cart-button" href="#" target="_blank">加入购物车</a>
                      <a href="#" target="_blank" class="price">￥498.00</a>
                    </li>-->
                    </ul>
                    <a
                      href="#"
                      class="history-bottom-more"
                      target="_blank"
                    >查看更多足迹商品 &gt;&gt;</a>
                  </div>
                </div>
              </div>
              <div class="tbar-panel-footer J-panel-footer" />
            </div>
          </div>

          <div class="toolbar-header" />

          <!-- 侧栏按钮 -->
          <div class="toolbar-tabs J-tab">
            <div
              onclick="cartPanelView.tabItemClick('cart')"
              class="toolbar-tab tbar-tab-cart"
              data="购物车"
              tag="cart"
            >
              <i class="tab-ico" />
              <em class="tab-text" />
              <span id="tab-sub-cart-count" class="tab-sub J-count">0</span>
            </div>
            <div
              onclick="cartPanelView.tabItemClick('follow')"
              class="toolbar-tab tbar-tab-follow"
              data="我的关注"
              tag="follow"
            >
              <i class="tab-ico" />
              <em class="tab-text" />
              <span class="tab-sub J-count hide">0</span>
            </div>
            <div
              onclick="cartPanelView.tabItemClick('history')"
              class="toolbar-tab tbar-tab-history"
              data="我的足迹"
              tag="history"
            >
              <i class="tab-ico" />
              <em class="tab-text" />
              <span class="tab-sub J-count hide">0</span>
            </div>
          </div>

          <div class="toolbar-footer">
            <div class="toolbar-tab tbar-tab-top">
              <a href="#">
                <i class="tab-ico" /> <em class="footer-tab-text">顶部</em>
              </a>
            </div>
            <div class="toolbar-tab tbar-tab-feedback">
              <a href="#" target="_blank">
                <i class="tab-ico" /> <em class="footer-tab-text">反馈</em>
              </a>
            </div>
          </div>

          <div class="toolbar-mini" />
        </div>

        <div id="J-toolbar-load-hook" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'Search',
  data() {
    return {
      result: {
        rows: []
      },
      searchMap: {},
      url: '',
      pageNo: 0,
      startPage: 0,
      endPage: 0,
      searchKeyWrd: this.$route.query.searchMsg,
      username: null,
      indexCategoryList: [],
      ticket: ''
    }
  },
  computed: {
    orderUrl() {
      const url = 'http://localhost:8080/user/findOrder'
      return this.username ? `${url}?username=${this.username}` : url
    }
  },
  created() {
    this.ticket = sessionStorage.getItem('ticket')
    this.getSearch('search?keywords=' + this.searchKeyWrd)
    this.indexCategoryList = JSON.parse(sessionStorage.getItem('indexCategoryList'))
    const usrname = sessionStorage.getItem('usrname')
    if (usrname) {
      this.username = usrname
    } else {
      if (this.getUrlVal()['username']) {
        this.username = this.getUrlVal()['username']
        sessionStorage.setItem('usrname', this.getUrlVal()['username'])
      }
    }
  },
  methods: {
    logout() {
      sessionStorage.removeItem('usrname')
      window.location.href = 'http://localhost:8080/cas/logout?service=http://localhost:8080/'
    },
    toCar(id) {
      axios({
        method: 'get',
        url: `http://localhost:8080/buyNew?skuId=${id}&num=1&username=${this.username}`,
        headers: {
          ticket: this.ticket
        }
      }).then(response => {
        window.location.href = 'http://localhost:8080/cart.html'
      })
    },
    getUrlVal() {
      const vars = {}
      const parts = window.location.href
      parts.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m, key, value) {
        vars[key] = value
      })
      return vars
    },
    getSearch(url) {
      axios.get(url).then((res) => {
        const data = res.data
        if (data.state === 1) {
          const ctn = data.content
          // 调用结果
          this.result = ctn.result
          // 查询对象
          this.searchMap = ctn.searchMap
          // url字符串
          this.url = axios.defaults.baseURL + ctn.url
          // 当前页
          this.pageNo = ctn.pageNo
          // 开始页
          this.startPage = ctn.startPage
          // 结束页
          this.endPage = ctn.endPage
        }
      })
    }
  }
}
</script>
