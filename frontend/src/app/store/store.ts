import {combineReducers, configureStore} from '@reduxjs/toolkit';
import counterReducer from '../features/counterSlice';
import authReducer from '../features/authSlice';
import { persistReducer, persistStore } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import thunk from 'redux-thunk';
// Redux 스토어 생성
const persistConfig = {
    key: 'root',
    storage, // LocalStorage 사용
    whitelist: ['counter','auth'], // 'counter' 리듀서만 저장
};


const rootReducer = combineReducers({
    counter: counterReducer,
    auth: authReducer,
});
// Redux Persist와 Root Reducer 통합
const persistedReducer = persistReducer(persistConfig, rootReducer);

const store = configureStore({
    reducer: {
        counter: persistedReducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {
                ignoredActions: [
                    'persist/PERSIST',
                    'persist/REHYDRATE',
                    'persist/REGISTER',
                    'persist/FLUSH',
                    'persist/PAUSE',
                    'persist/PURGE',
                ],
            },
            thunk,
        }),
});

export const persistor = persistStore(store);
export default store;
// RootState와 AppDispatch 타입 정의
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

