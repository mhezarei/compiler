; ModuleID = 'test1.ll'
source_filename = "test1.cpp"
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; Function Attrs: noinline norecurse nounwind optnone uwtable
define i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i8, align 1
  %3 = alloca i8, align 1
  %4 = alloca double, align 8
  %5 = alloca i32, align 4
  %6 = alloca float, align 4
  %7 = alloca float, align 4
  store i32 0, i32* %1, align 4
  store i8 115, i8* %3, align 1
  store double 9.000000e+00, double* %4, align 8
  %8 = load i32, i32* %5, align 4
  %9 = add nsw i32 2, %8
  %10 = sitofp i32 %9 to float
  store float %10, float* %7, align 4
  %11 = load float, float* %6, align 4
  %12 = fmul float 1.000000e+00, %11
  %13 = load float, float* %7, align 4
  %14 = fadd float %12, %13
  %15 = fptosi float %14 to i32
  store i32 %15, i32* %5, align 4
  %16 = load i32, i32* %5, align 4
  %17 = sitofp i32 %16 to float
  %18 = load float, float* %6, align 4
  %19 = fcmp oeq float %17, %18
  br i1 %19, label %20, label %21

; <label>:20:                                     ; preds = %0
  store float 3.000000e+00, float* %6, align 4
  br label %22

; <label>:21:                                     ; preds = %0
  store float 4.000000e+00, float* %6, align 4
  br label %22

; <label>:22:                                     ; preds = %21, %20
  %23 = load float, float* %7, align 4
  %24 = fptosi float %23 to i32
  ret i32 %24
}

attributes #0 = { noinline norecurse nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 6.0.0-1ubuntu2 (tags/RELEASE_600/final)"}
