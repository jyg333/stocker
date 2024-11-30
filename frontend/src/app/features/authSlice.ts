import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
    accessToken: string | null;
    member : string | null;

}

const initialState: AuthState = {
    accessToken: null,
    member : null

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
        setMember: (state, action: PayloadAction<string>)  => {
            state.member = action.payload
        }
    },
});

export const { setTokens, clearTokens, setMember } = authSlice.actions;

export default authSlice.reducer;
