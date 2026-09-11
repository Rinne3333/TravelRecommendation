// 地图API配置
export const MAP_CONFIG = {
  // 高德地图API Key (请替换为您的真实API Key)
  AMAP_API_KEY: "6d672e6194c34c2d9e5c8d6c2b8f8a8a",

  // 地图版本
  AMAP_VERSION: "2.0",

  // 地图插件
  AMAP_PLUGINS: [
    "AMap.Scale",
    "AMap.ToolBar",
    "AMap.Driving",
    "AMap.TruckDriving",
    "AMap.Geocoder",
    "AMap.AutoComplete",
    "AMap.PlaceSearch"
  ],

  // 默认地图配置
  DEFAULT_MAP_OPTIONS: {
    zoom: 10,
    center: [116.397477, 39.909187], // 北京天安门
    mapStyle: "amap://styles/normal",
    resizeEnable: true,
    rotateEnable: true,
    pitchEnable: true,
    buildingAnimation: true,
    expandZoomRange: true,
    zooms: [3, 20]
  },

  // 路线样式配置
  ROUTE_STYLE: {
    strokeColor: "#3366FF",
    strokeWeight: 6,
    strokeOpacity: 0.8,
    strokeStyle: "solid"
  },

  // 标记图标配置
  MARKER_CONFIG: {
    size: 32,
    colors: {
      start: "#52c41a",
      end: "#f5222d",
      waypoint: "#1890ff"
    }
  }
};

// 景点坐标数据库
export const SPOT_COORDINATES: { [key: string]: [number, number] } = {
  // 北京景点
  天安门广场: [116.397477, 39.909187],
  故宫博物院: [116.397026, 39.918058],
  天坛公园: [116.407394, 39.883119],
  颐和园: [116.275, 39.99],
  长城: [116.565, 40.376],
  北海公园: [116.388, 39.928],
  景山公园: [116.395, 39.926],
  雍和宫: [116.418, 39.948],

  // 桂林景点
  漓江: [110.179953, 25.273566],
  象鼻山: [110.269, 25.274],
  阳朔西街: [110.498, 24.779],
  遇龙河: [110.441, 24.797],
  银子岩: [110.378, 24.926],
  七星公园: [110.323, 25.252],
  叠彩山: [110.298, 25.285],

  // 西安景点
  兵马俑: [109.273, 34.385],
  华清宫: [109.212, 34.362],
  大雁塔: [108.964, 34.218],
  古城墙: [108.946, 34.266],
  回民街: [108.941, 34.263],
  小雁塔: [108.945, 34.247],
  陕西历史博物馆: [108.953, 34.236],

  // 张家界景点
  张家界国家森林公园: [110.479, 29.325],
  天门山: [110.472, 29.325],
  黄龙洞: [110.417, 29.325],
  金鞭溪: [110.442, 29.325],
  袁家界: [110.439, 29.344],
  杨家界: [110.425, 29.352],

  // 苏州景点
  拙政园: [120.629, 31.326],
  留园: [120.618, 31.315],
  虎丘: [120.595, 31.324],
  平江路: [120.625, 31.316],
  周庄古镇: [120.849, 31.115],
  狮子林: [120.624, 31.323],
  网师园: [120.627, 31.307],

  // 三亚景点
  亚龙湾: [109.645, 18.231],
  天涯海角: [109.347, 18.298],
  南山寺: [109.355, 18.307],
  蜈支洲岛: [109.769, 18.313],
  大东海: [109.496, 18.214],
  鹿回头: [109.511, 18.206],

  // 四川景点
  九寨沟: [103.919, 33.152],
  黄龙: [103.832, 32.755],
  都江堰: [103.647, 31.023],
  青城山: [103.567, 30.909],
  峨眉山: [103.334, 29.601],
  乐山大佛: [103.771, 29.545],

  // 云南景点
  丽江古城: [100.233, 26.872],
  玉龙雪山: [100.183, 27.106],
  泸沽湖: [100.787, 27.693],
  束河古镇: [100.208, 26.896],
  大理古城: [100.225, 25.606],
  洱海: [100.236, 25.59],
  石林: [103.347, 24.814],

  // 上海景点
  外滩: [121.485, 31.24],
  东方明珠: [121.506, 31.245],
  南京路: [121.479, 31.235],
  豫园: [121.492, 31.226],
  田子坊: [121.466, 31.21],
  新天地: [121.477, 31.219],

  // 杭州景点
  西湖: [120.13, 30.259],
  雷峰塔: [120.149, 30.231],
  灵隐寺: [120.101, 30.242],
  千岛湖: [119.023, 29.605],
  宋城: [120.087, 30.193],

  // 厦门景点
  鼓浪屿: [118.063, 24.448],
  南普陀寺: [118.095, 24.445],
  厦门大学: [118.096, 24.438],
  环岛路: [118.132, 24.473],
  曾厝垵: [118.114, 24.457]
};

// 获取景点坐标
export const getSpotCoordinate = (
  spotName: string
): [number, number] | null => {
  return SPOT_COORDINATES[spotName] || null;
};

// 获取多个景点的坐标
export const getSpotsCoordinates = (
  spotNames: string[]
): Array<{
  name: string;
  coordinate: [number, number];
}> => {
  return spotNames
    .map((name) => ({
      name,
      coordinate: getSpotCoordinate(name)
    }))
    .filter((item) => item.coordinate !== null) as Array<{
    name: string;
    coordinate: [number, number];
  }>;
};

// 计算两点间距离（简单计算）
export const calculateDistance = (
  coord1: [number, number],
  coord2: [number, number]
): number => {
  const [lng1, lat1] = coord1;
  const [lng2, lat2] = coord2;

  const radLat1 = (lat1 * Math.PI) / 180;
  const radLat2 = (lat2 * Math.PI) / 180;
  const deltaLat = radLat1 - radLat2;
  const deltaLng = ((lng1 - lng2) * Math.PI) / 180;

  const distance =
    2 *
    Math.asin(
      Math.sqrt(
        Math.pow(Math.sin(deltaLat / 2), 2) +
          Math.cos(radLat1) *
            Math.cos(radLat2) *
            Math.pow(Math.sin(deltaLng / 2), 2)
      )
    ) *
    6378137; // 地球半径

  return Math.round(distance / 1000); // 转换为公里
};
