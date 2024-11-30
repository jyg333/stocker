import { combineReducers, configureStore } from '@reduxjs/toolkit';
import counterReducer from '../features/counterSlice';
import authReducer from '../features/authSlice';
import { persistReducer, persistStore } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import thunk from 'redux-thunk';

// Redux Persist Configuration
const persistConfig = {
    key: 'root',
    storage, // LocalStorage 사용
    whitelist: ['counter', 'auth'], // 'counter'와 'auth' 리듀서를 저장
};

// Combine Reducers
const rootReducer = combineReducers({
    counter: counterReducer,
    auth: authReducer, // auth 리듀서를 포함
});

// Persist Reducer 적용
const persistedReducer = persistReducer(persistConfig, rootReducer);

// Redux Store 생성
const store = configureStore({
    reducer: persistedReducer, // rootReducer를 직접 설정
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

// RootState와 AppDispatch 타입 정의
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;
