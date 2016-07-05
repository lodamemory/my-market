<template>
<div class="page-header-fixed page-quick-sidebar-over-content page-header-fixed-mobile page-footer-fixed1">
    
    <m-header></m-header>
    <m-container></m-container>
    <m-footer></m-footer>
</div>
</template>
<script>
    import mHeader from './common/mHeader.vue';
    import mContainer from './common/mContainer.vue';
    import mFooter from './common/mFooter.vue';
    import mButton from './components/mButton.vue';
    export default{
        components: {mHeader, mContainer, mFooter, mButton},
        data () {
            return {
                header: '导航1'
            };
        }
    };
</script>


