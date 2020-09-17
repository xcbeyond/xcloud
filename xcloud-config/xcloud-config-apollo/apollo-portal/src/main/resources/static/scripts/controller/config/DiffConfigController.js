diff_item_module.controller("DiffItemController",
    ['$scope', '$location', '$window', '$translate', 'toastr', 'AppService', 'AppUtil', 'ConfigService',
        function ($scope, $location, $window, $translate, toastr, AppService, AppUtil, ConfigService) {

            var params = AppUtil.parseParams($location.$$url);
            $scope.pageContext = {
                appId: params.appid,
                env: params.env,
                clusterName: params.clusterName,
                namespaceName: params.namespaceName
            };
            var sourceItems = [];

            $scope.diff = diff;
            $scope.syncBtnDisabled = false;
            $scope.showCommentDiff = false;

            $scope.collectSelectedClusters = collectSelectedClusters;

            $scope.syncItemNextStep = syncItemNextStep;
            $scope.backToAppHomePage = backToAppHomePage;
            $scope.switchSelect = switchSelect;

            $scope.showText = showText;

            $scope.itemsKeyedByKey = {};

            $scope.syncData = {
                syncToNamespaces: [],
                syncItems: []
            };

            function diff() {
                $scope.syncData = parseSyncSourceData();
                if ($scope.syncData.syncToNamespaces.length < 2) {
                    toastr.warning($translate.instant('Config.Diff.PleaseChooseTwoCluster'));
                    return;
                }
                $scope.syncData.syncToNamespaces.forEach(function (namespace) {
                    ConfigService.find_items(namespace.appId,
                        namespace.env,
                        namespace.clusterName,
                        namespace.namespaceName).then(function (result) {
                            result.forEach(function (item) {
                                var itemsKeyedByClusterName = $scope.itemsKeyedByKey[item.key] || {};
                                itemsKeyedByClusterName[namespace.env + ':' + namespace.clusterName + ':' + namespace.namespaceName] = item;
                                $scope.itemsKeyedByKey[item.key] = itemsKeyedByClusterName;
                            });
                        });
                });
                $scope.syncItemNextStep(1);
            }

            var selectedClusters = [];

            function collectSelectedClusters(data) {
                selectedClusters = data;
            }

            function parseSyncSourceData() {
                var syncData = {
                    syncToNamespaces: [],
                    syncItems: []
                };
                var namespaceName = $scope.pageContext.namespaceName;
                selectedClusters.forEach(function (cluster) {
                    if (cluster.checked) {
                        cluster.clusterName = cluster.name;
                        cluster.namespaceName = namespaceName;
                        syncData.syncToNamespaces.push(cluster);
                    }
                });

                return syncData;
            }

            ////// flow control ///////

            $scope.syncItemStep = 1;
            function syncItemNextStep(offset) {
                $scope.syncItemStep += offset;
            }

            function backToAppHomePage() {
                $window.location.href = AppUtil.prefixPath() + '/config.html?#appid=' + $scope.pageContext.appId;
            }

            function switchSelect(o) {
                o.checked = !o.checked;
            }

            function showText(text) {
                $scope.text = text;
                AppUtil.showModal('#showTextModal');
            }
        }]);
