import App from './App';
export default function (router) {
    router.map({
        '/': {
            name: 'app',
            component: App
        }
    });
}