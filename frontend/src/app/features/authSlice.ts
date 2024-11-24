import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
    accessToken: string | null;

}

const initialState: AuthState = {
    accessToken: null,

};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setTokens: (state, action: PayloadAction<{ accessToken: string }>) => {
            state.accessToken = action.payload.accessToken;
        },
        clearTokens: (state) => {
            state.accessToken = null;
        },
    },
});

export const { setTokens, clearTokens } = authSlice.actions;

export default authSlice.reducer;
